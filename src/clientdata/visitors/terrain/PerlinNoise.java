package clientdata.visitors.terrain;

@SuppressWarnings("unused")

public class PerlinNoise {

	private static int PB = 0x100;
	private static int PBM = 0xff;
	private static int PN = 0x1000;
	private static int PNP = 12;
	private static int PNM = 0xfff;
	
	int p[];
	float g2[][];
	float g1[];
	boolean needsStart;
	
	SWGRandom rand;
	
	public PerlinNoise(SWGRandom r) {
		p = new int[PB+PB+2];
		g2 = new float[PB+PB+2][2];
		g1 = new float[PB+PB+2];
		
		needsStart = true;
		rand = r;
	}
	
	private double s_curve(double t) {
		return t * t * (3.0 - 2.0 * t);
	}
	
	private double lerp(double t, double a,  double b) {
		return a + t*(b-a);
	}
	
	public float noise2(double vec[]) {
		
		int bx0, bx1, by0, by1, b00, b10, b01, b11;
		double rx0, rx1, ry0, ry1;
		double t, sx, sy, a, b, u, v;
		int i, j;
		
		if(needsStart) {
			needsStart = false;
			init();
		}
		
		t = (double)vec[0] + (double)PN;
		bx0 = ((int)t) & PBM;
		bx1 = (bx0+1) & PBM;
		rx0 = t - (int)t;
		rx1 = rx0 - 1.0;
		
		t = (double)vec[1] + (double)PN;
		by0 = ((int)t) & PBM;
		by1 = (by0+1) & PBM;
		ry0 = t - (int)t;
		ry1 = ry0 - 1.0;
		
		i = p[ bx0 ];
		j = p[ bx1 ];
		
		b00 = p[ i + by0 ];
		b10 = p[ j + by0 ];
		b01 = p[ i + by1 ];
		b11 = p[ j + by1 ];

		sx = s_curve(rx0);
		sy = s_curve(ry0);
		
		u = rx0 * g2[b00][0] + ry0 * g2[b00][1];
		v = rx1 * g2[b10][0] + ry0 * g2[b10][1];
		a = lerp(sx, u, v);
		
		u = rx0 * g2[b01][0] + ry1 * g2[b01][1];
		v = rx1 * g2[b11][0] + ry1 * g2[b11][1];
		b = lerp(sx, u, v);
		
		return (float) lerp(sy, a, b);
	}
	
	static void normalize2(float v[]) {
		double s;
		
		s = Math.sqrt(v[0] * v[0] + v[1] * v[1]);
		v[0] = (float) (v[0] / s);
		v[1] = (float) (v[1] / s);
	}
	
	void init() {
		int i, j, k;
		
		for(i = 0; i < PB; ++i) {
			p[i] = i;
			
			g1[i] = (float)(double)((rand.next() % (PB + PB)) - PB) / PB;
			
			for(j = 0; j < 2; ++j) {
				g2[i][j] = (float) ((double)((rand.next() % (PB + PB)) - PB) / PB);
			}
			
			normalize2(g2[i]);
		}
		
		while(--i != 0) {
			k = p[i];
			p[i] = p[ j = rand.next() % PB];
			p[j] = k;
		}
		
		for(i = 0; i < PB + 2; ++i) {
			p[PB + i] = p[i];
			g1[PB + i] = g1[i];
			
			for(j = 0; j < 2; ++j) {
				g2[PB + i][j] = g2[i][j];
			}
		}
	}
}
