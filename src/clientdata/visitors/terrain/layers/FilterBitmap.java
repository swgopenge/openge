package clientdata.visitors.terrain.layers;

import org.apache.mina.core.buffer.IoBuffer;
import clientdata.visitors.TerrainVisitor;
import clientdata.visitors.terrain.TargaBitmap;

/*
	

    double __userpurge sub_6A08D0<st0>(int a1<ecx>, int a2<esi>, float a3, int a4, int a5, int a6, float a7)
    {
      int v7; // ebp@1
      int v8; // ST0C_4@1
      float v9; // eax@1
      float v11; // edx@3
      signed int v12; // ebx@3
      int v13; // esi@3
      double v14; // st7@3
      double v15; // st7@3
      float *v16; // eax@7
      double v17; // st7@9
      float *v18; // eax@9
      signed int v19; // ebp@11
      float *v20; // eax@11
      float *v21; // eax@13
      int v22; // edi@15
      int v23; // eax@15
      int v24; // ecx@15
      int v25; // esi@15
      int v26; // edx@15
      int v27; // eax@15
      unsigned __int8 v28; // bl@15
      double v29; // st7@15
      double v30; // st6@15
      double v31; // st4@15
      double v32; // st5@15
      double v33; // qt0@15
      double v34; // st7@15
      double v35; // st7@19
      int v36; // [sp+8h] [bp-3Ch]@3
      signed int v37; // [sp+18h] [bp-2Ch]@7
      float v38; // [sp+1Ch] [bp-28h]@9
      float v39; // [sp+20h] [bp-24h]@7
      int v40; // [sp+24h] [bp-20h]@3
      float v41; // [sp+28h] [bp-1Ch]@1
      float v42; // [sp+2Ch] [bp-18h]@11
      int v43; // [sp+30h] [bp-14h]@1
      float v44; // [sp+3Ch] [bp-8h]@3
      float v45; // [sp+40h] [bp-4h]@3
     
      v7 = a1;
      v8 = *(_DWORD *)(a1 + 28);
      v43 = a1;
      LODWORD(v9) = sub_677750(v8);
      v41 = v9;
      if ( !LODWORD(v9) )
        return 1.0;
      LODWORD(v11) = *LODWORD(v9);
      v12 = *(_DWORD *)(LODWORD(v9) + 16);
      v36 = a2;
      v13 = *(_DWORD *)(LODWORD(v9) + 4);
      v44 = *(float *)(v7 + 48) - *(float *)(v7 + 40);
      v14 = *(float *)(v7 + 52);
      a7 = v11;
      v15 = v14 - *(float *)(v7 + 44);
      v40 = v13;
      v45 = v15;
      if ( 0.0 == v44 )
        DebugFatal("FilterBitmap::isWithin: rect.x1 is 0.0f", v36);
      if ( 0.0 == v45 )
        DebugFatal("FilterBitmap::isWithin: rect.y1 is 0.0f", v36);
      v39 = (double)SLODWORD(a7);
      *(float *)&v37 = v39 - 1.0;
      a3 = v39 * (a3 - *(float *)(v7 + 40)) / v44;
      v16 = (float *)&v37;
      if ( a3 <= (double)*(float *)&v37 )
        v16 = &a3;
      v38 = *v16;
      a3 = (double)v40;
      v17 = a3;
      a3 = a3 - 1.0;
      *(float *)&a4 = v17 * (*(float *)&a4 - *(float *)(v7 + 44)) / v45;
      v18 = &a3;
      if ( *(float *)&a4 <= (double)a3 )
        v18 = (float *)&a4;
      v39 = *v18;
      v37 = (signed int)v38;
      v19 = (signed int)v39;
      LODWORD(a3) = LODWORD(a7) - 1;
      LODWORD(v42) = (signed int)v39;
      a4 = v37 + 1;
      v20 = &a3;
      if ( LODWORD(a7) - 1 >= v37 + 1 )
        v20 = (float *)&a4;
      v40 = *(_DWORD *)v20;
      LODWORD(a3) = v13 - 1;
      a4 = v19 + 1;
      v21 = &a3;
      if ( v13 - 1 >= v19 + 1 )
        v21 = (float *)&a4;
      a4 = *(_DWORD *)v21;
      v22 = sub_4C9410(0);
      v23 = v12 / SLODWORD(a7);
      v24 = v12 * (v13 - v19 - 1);
      v25 = v12 * (v13 - a4 - 1);
      v26 = v37 * v12 / SLODWORD(a7);
      LOBYTE(a7) = *(_BYTE *)(v26 + v24 + v22);
      v27 = v40 * v23;
      LOBYTE(v24) = *(_BYTE *)(v27 + v24 + v22);
      v28 = *(_BYTE *)(v25 + v26 + v22);
      LOBYTE(v27) = *(_BYTE *)(v27 + v25 + v22);
      LOBYTE(a3) = v24;
      LOBYTE(a4) = v27;
      sub_4C9460(LODWORD(v41));
      v41 = v38 - (double)v37;
      v29 = v41;
      v41 = 1.0 - v41;
      v42 = v39 - (double)SLODWORD(v42);
      v30 = v42;
      v42 = 1.0 - v42;
      a7 = (double)LOBYTE(a7) * 0.003921568859368563;
      v31 = v42 * v41 * a7;
      a7 = (double)LOBYTE(a3) * 0.003921568859368563;
      v32 = v42 * v29 * a7 + v31;
      a7 = (double)v28 * 0.003921568859368563;
      v33 = v32 + v41 * v30 * a7;
      a7 = 0.003921568859368563 * (double)(unsigned __int8)a4;
      a7 = v29 * v30 * a7 + v33;
      a7 = a7 + *(float *)(v43 + 56);
      v34 = 0.0;
      if ( a7 < 0.0 )
        goto LABEL_18;
      if ( a7 >= 1.0 )
      {
        v34 = 0.99998999;
    LABEL_18:
        a7 = v34;
      }
      a3 = *(float *)(v43 + 24);
      v35 = *(float *)(v43 + 32);
      sub_69F440(*(float *)(v43 + 32), a7, *(float *)(v43 + 36), a3);
      return (float)(v35 * a7);
    }


*/

public class FilterBitmap extends FilterLayer {
	
	private int bitmapId;
	private float min, max, gain;
	private TargaBitmap bitmap;
	
	public FilterBitmap() {
	}

	@Override
	public void loadData(IoBuffer buffer) throws Exception {
		this.bitmapId = buffer.getInt();
		this.feather_type = buffer.getInt();
		this.feather_amount = buffer.getFloat();
		this.min = buffer.getFloat();
		this.max = buffer.getFloat();
		if(buffer.remaining() >= 4) 
			this.gain = buffer.getFloat();
	}

	@Override
	public float process(float x, float z, float transform_value, float base_value, TerrainVisitor ti, FilterRectangle rectangle) {
		if(bitmap == null) {
			bitmap = ti.getBitmapGroup().getBitmaps().get(bitmapId);
			if(bitmap == null) {
				System.out.println("Didnt find bitmap for filter");
				return 1;
			}
		}
		//System.out.println("Using bitmap: " + bitmap.getFileName());
		//System.out.println("rect minx : " + rectangle.minX + " rect minZ : " + rectangle.minZ + " rect maxX: " + rectangle.maxX + " rect maxZ: " + rectangle.maxZ);
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();
		float same_as_width_height = width;
		
		long v44 = (long) same_as_width_height;
		float arg6 = width;
		
		float transformedX = (x - rectangle.minX) * width / (rectangle.maxX - rectangle.minX);
		float transformedZ = (z - rectangle.minZ) * height / (rectangle.maxZ - rectangle.minZ);
		
		if (transformedX > width - 1) {
			System.out.println("transformed x exceeded boundary");
			transformedX = width - 1;
		}
		if (transformedZ > height - 1) {
			System.out.println("transformed z exceeded boundary");
			transformedZ = height - 1;
		}
		
		int v41 = (int) transformedZ;
		float v39 = transformedX + 1;
		int v43 = (int) transformedX;
		float v42 = transformedX;
		float v40 = transformedZ;
		float arg1 = (int) transformedZ + 1;
		int v25 = (int) (v44 * (height - v41 - 1));
		int v26 = (int) (v39 * v44 / arg6);
		int v27 = (int) (v43 * v44 / arg6);
		
		float v28 = bitmap.getData(v27 + v25) & 0xFF;
		//byte mapValue = v28;
		float arg2 = 0;
		arg2 = bitmap.getData(v26 + v25) & 0xFF;
		int v29 = (int) (v44 * (height - arg1 - 1));
		v25 = bitmap.getData(v29 + v27) & 0xFF;
		v27 = bitmap.getData(v26 + v29) & 0xFF;
		arg6 = v25;
		arg1 = v27;
		double v30 = v42 - (double) v43;
		double v31 = v40 - (double) v41;
		float mapResult = (float) (v31 * (1.0 - v30) * arg6 * 0.003921568859368563
				 + v31 * v30 * arg1 * 0.003921568859368563
				 + (1.0 - v31) * (1.0 - v30) * v28 * 0.003921568859368563
				 + (1.0 - v31) * v30 * arg2 * 0.003921568859368563);
		mapResult += gain;	
		if (mapResult >= 1.0)
			mapResult = (float) 0.9999899864196777;
		if (mapResult < 0)
			mapResult = 0;
		float v32 = feather_amount;
		float v33 = max;
		arg6 = min;
		arg2 = v32;
		arg1 = v33;
		float result = 0;
		//System.out.println("map result: " + mapResult + " min: " + min + " max: " + max);
		
		if (mapResult > arg6 && mapResult < arg1) {
			float v35 = (float) ((arg1 - arg6) * arg2 * 0.5);
			if (mapResult >= arg6 + v35) {
				if (mapResult <= arg1 - v35)
					result = mapResult;
				else
					result = mapResult * (arg1 - mapResult) / v35;
			} else {
				result = mapResult * (mapResult - arg6) / v35;
				//result = 0;
			}
			
		}
		return result;
	}

	@Override
	public float process(float x, float y, float transform_value,
			float base_value, TerrainVisitor ti) {
		// TODO Auto-generated method stub
		return 0;
	}

}
