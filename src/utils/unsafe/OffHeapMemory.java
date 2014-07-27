package utils.unsafe;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.sun.management.*;

import sun.misc.Unsafe;

public class OffHeapMemory {
		
	private static Unsafe unsafe;
	private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
	private static HotSpotDiagnosticMXBean hotspotMBean;
	private static Architecture arch;
	private static int classDefPointerOffset;
	private static int objectClassDefPointerOffset;
	private static int sizeFieldOffset;
	private static int oopSize;
    
	public enum Architecture {
		X86,
		X64,
		X64WITHCOMPRESSEDOOPS,
		X64WITH32BITCOMPRESSEDOOPS
	}
	
	static {
		
			
    	Field f;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
	    	f.setAccessible(true);
	    	Unsafe unsafe2 = (Unsafe) f.get(null);
	    	unsafe = unsafe2;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
        long off1 = 0;
        long off2 = 0;
		try {
			off1 = unsafe.objectFieldOffset(Pointer.class.getField("object"));
			off2 = unsafe.objectFieldOffset(Pointer.class.getField("address"));
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}        
		System.out.println(getHotSpotMBean().getVMOption("Xmx").getValue());
        oopSize = (int) Math.abs(off2 - off1);
		String bits = System.getProperty("sun.arch.data.model");
		if(bits.equals("32")) { 
			objectClassDefPointerOffset = 4;
			sizeFieldOffset = 12;
			classDefPointerOffset = 80;
			arch = Architecture.X86;
		} else if(bits.equals("64") && getUseCompressedOopsVMOption()) {
			objectClassDefPointerOffset = 8;
			sizeFieldOffset = 24;
			//arch = oopSize == 4 ? Architecture.X64WITH32BITCOMPRESSEDOOPS : Architecture.X64WITHCOMPRESSEDOOPS;
			arch = Runtime.getRuntime().maxMemory() <= 2058354688 ? Architecture.X64WITH32BITCOMPRESSEDOOPS : Architecture.X64WITHCOMPRESSEDOOPS;
			classDefPointerOffset = 84;
		} else {
			objectClassDefPointerOffset = 8;
			sizeFieldOffset = 24;
			classDefPointerOffset = 160;
			arch = Architecture.X64;		
		}

    }
	private static <T> long getCompressedAddressByShifting(T obj, int compressOopShift) {
		T[] array = (T[]) new Object[] { obj };
		int baseOffset = unsafe.arrayBaseOffset(Object[].class);
		return normalize(unsafe.getInt(array, baseOffset))/* << compressOopShift*/;
	}
	/**
	 * @param size the amount of memory to be allocated
	 * @return the pointer to the allocated memory
	 */
	public static long malloc(long size) {
		return unsafe.allocateMemory(size);
	}
	
	/**
	 * Frees the memory located at the address
	 * @param address the address of the memory which needs to be freed
	 */
	public static void free(long address) {
		unsafe.freeMemory(address);
	}
	
	/**
	 * Allocates a char off heap
	 * @param c the char to be allocated
	 * @return the pointer to the char 
	 */
	public static long allocateChar(char c) {
		long pointer = malloc(2);
		unsafe.putChar(pointer, c);
		return pointer;
	}
	
	/**
	 * Allocates a byte off heap
	 * @param b the byte to be allocated
	 * @return the pointer to the byte 
	 */
	public static long allocateByte(byte b) {
		long pointer = malloc(1);
		unsafe.putByte(pointer, b);
		return pointer;
	}
	
	/**
	 * Allocates a short off heap
	 * @param s the short to be allocated
	 * @return the pointer to the short 
	 */
	public static long allocateShort(short s) {
		long pointer = malloc(2);
		unsafe.putShort(pointer, s);
		return pointer;
	}
	
	/**
	 * Allocates an int off heap
	 * @param i the int to be allocated
	 * @return the pointer to the int 
	 */
	public static long allocateInt(int i) {
		long pointer = malloc(4);
		unsafe.putInt(pointer, i);
		return pointer;
	}

	/**
	 * Allocates an long off heap
	 * @param l the long to be allocated
	 * @return the pointer to the long 
	 */
	public static long allocateLong(int l) {
		long pointer = malloc(8);
		unsafe.putInt(pointer, l);
		return pointer;
	}
	
	/**
	 * Allocates an float off heap
	 * @param f the float to be allocated
	 * @return the pointer to the float 
	 */
	public static long allocateFloat(float f) {
		long pointer = malloc(4);
		unsafe.putFloat(pointer, f);
		return pointer;
	}

	/**
	 * Allocates an double off heap
	 * @param d the double to be allocated
	 * @return the pointer to the double 
	 */
	public static long allocateDouble(double d) {
		long pointer = malloc(8);
		unsafe.putDouble(pointer, d);
		return pointer;
	}
	
	/**
	 * Allocates an object off heap
	 * @param o the object to be allocated
	 * @return the pointer to the object 
	 */

	public static Pointer allocateObject(Object obj) {
		long size = sizeOf(obj.getClass());
		Pointer pointerObj = null;
		try {
			pointerObj = (Pointer) unsafe.allocateInstance(Pointer.class);
			pointerObj.address = malloc(size);
			unsafe.copyMemory(obj, 0, null, pointerObj.address, size);
			long objOffset = getUnsafe().objectFieldOffset(Pointer.class.getDeclaredField("object"));
			// at around 2 GB or smaller heaps the JVM will use 32 bit pointers completely so no oop shift is required
			// this will work upto 32 GB, after that the JVM has to use a base offset to compensate, and after 64 GB we have to shift left by 4 instead of 3
			// unfortunately there is no easy way to get the oops base offset, so users with heaps > 32 GB will have to add -XX:-UseCompressedOops to start the server
			if(arch == Architecture.X64WITHCOMPRESSEDOOPS)
				getUnsafe().putLong(pointerObj, objOffset, pointerObj.address >> 3); // set pointer to off-heap copy of the object	
			else
				getUnsafe().putLong(pointerObj, objOffset, pointerObj.address);
		} catch (InstantiationException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return pointerObj;
	}
	
	 private static HotSpotDiagnosticMXBean getHotSpotMBean() {
		 if (hotspotMBean == null) {
			 try {
				 hotspotMBean = ManagementFactory.newPlatformMXBeanProxy(
						 ManagementFactory.getPlatformMBeanServer(),
						 HOTSPOT_BEAN_NAME,
						 HotSpotDiagnosticMXBean.class);
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }
		 return hotspotMBean;
	 }
	 
	 public static boolean getUseCompressedOopsVMOption() {
		 return Boolean.valueOf(getHotSpotMBean().getVMOption("UseCompressedOops").getValue());
	 }
	
	/**
	 * DO NOT CALL THIS UNLESS YOU ARE SURE THE OBJECT IS OFF HEAP OTHERWISE EVERYTHING EXPLODES
	 * @param object the object to be freed
	 */
	public static void free(Object object) {
		unsafe.freeMemory(toAddress(object));
	}
			
    private static long normalize(int value) {
        return value & 0xFFFFFFFFL;
    }
    
    public static long toAddress(Object obj) {
        Object[] array = new Object[] {obj};
        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        switch(arch) {
        case X86:
        	return normalize(unsafe.getInt(array, baseOffset));
        case X64:
        	return unsafe.getLong(array, baseOffset);
        case X64WITHCOMPRESSEDOOPS:
        	return (normalize(unsafe.getInt(array, baseOffset)) << 3);        	
        default:
        	return normalize(unsafe.getInt(array, baseOffset));        	
        	
        }
    }

    /**
     * This assumes that the address has already been converted for 32 bit and compressed oops VMs
     * @param address
     * @return
     */
    public static Object fromAddress(long address) {
        Object[] array = new Object[] {null};
        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        unsafe.putLong(array, baseOffset, address);
        switch(arch) {
        case X86:
            unsafe.putInt(array, baseOffset, (int) address);
        case X64:
            unsafe.putLong(array, baseOffset, address);
        case X64WITHCOMPRESSEDOOPS:
            unsafe.putInt(array, baseOffset, (int) address);
        default:
            unsafe.putInt(array, baseOffset, (int) address);
        	
        }

        return array[0];
    }
    
    public static long sizeOf(Object o) {
    	
    	long classAddress = addressOfClassBase(o.getClass());
    	return unsafe.getInt(classAddress + sizeFieldOffset);
    	/*
        HashSet<Field> fields = new HashSet<Field>();
        Class c = o.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    fields.add(f);
                }
            }
            c = c.getSuperclass();
        }

        // get offset
        long maxSize = 0;
        for (Field f : fields) {
            long offset = unsafe.objectFieldOffset(f);
            if (offset > maxSize) {
                maxSize = offset;
            }
        }

        return ((maxSize/8) + 1) * 8;   // padding*/
    }
    
    private static long addressOfClassBase(Class<? extends Object> class1) {
		Object[] objArray = new Object[1];
    	switch(arch) {
    	
    		case X64:
    			objArray[0] = class1;
    			return unsafe.getLong(objArray, unsafe.arrayBaseOffset(Object[].class));
    		case X64WITHCOMPRESSEDOOPS:
    			objArray[0] = class1;
    			return normalize(unsafe.getInt(objArray, unsafe.arrayBaseOffset(Object[].class))) << 3;
    		default:
    		case X86:
    			objArray[0] = class1;
    			return unsafe.getInt(objArray, unsafe.arrayBaseOffset(Object[].class));
    		
    	}
    	
	}

	public static long sizeOf(Class<?> clazz) {
	    long maximumOffset = 0;
	    do {
	      for (Field f : clazz.getDeclaredFields()) {
	        if (!Modifier.isStatic(f.getModifiers())) {
	          maximumOffset = Math.max(maximumOffset, unsafe.objectFieldOffset(f));
	        }
	      }
	    } while ((clazz = clazz.getSuperclass()) != null);
	    return maximumOffset + 8;
    }
    
    public static Unsafe getUnsafe() {
    	return unsafe;
    }


}
