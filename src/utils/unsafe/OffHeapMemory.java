package utils.unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;

import sun.misc.Unsafe;

public class OffHeapMemory {
	
	private static Unsafe unsafe;
	
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
		long size = sizeOf(obj);
		Pointer pointerObj = null;
		try {
			pointerObj = (Pointer) unsafe.allocateInstance(Pointer.class);
			pointerObj.address = malloc(size);
			unsafe.copyMemory(obj, 0, null, pointerObj.address, size);
			long objOffset = getUnsafe().objectFieldOffset(Pointer.class.getDeclaredField("object"));
			getUnsafe().putLong(pointerObj, objOffset, pointerObj.address); // set pointer to off-heap copy of the object			
		} catch (InstantiationException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return pointerObj;
	}
	
	/**
	 * DO NOT CALL THIS UNLESS YOU ARE SURE THE OBJECT IS OFF HEAP OTHERWISE EVERYTHING EXPLODES
	 * @param object the object to be freed
	 */
	public static void free(Object object) {
		unsafe.freeMemory(toAddress(object));
	}
			
    private static long normalize(int value) {
        if(value >= 0) return value;
        return (~0L >>> 32) & value;
    }
    
    public static long toAddress(Object obj) {
        Object[] array = new Object[] {obj};
        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        return normalize(unsafe.getInt(array, baseOffset));
    }

    public static Object fromAddress(long address) {
        Object[] array = new Object[] {null};
        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        unsafe.putLong(array, baseOffset, address);
        return array[0];
    }
    
    public static long sizeOf(Object o) {
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

        return ((maxSize/8) + 1) * 8;   // padding
    }
    
    public static Unsafe getUnsafe() {
    	return unsafe;
    }


}
