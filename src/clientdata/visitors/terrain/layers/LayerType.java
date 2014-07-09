package clientdata.visitors.terrain.layers;

public enum LayerType {
		NONE,
		CONTAINER,
		BREC, //Boundry Rectangle
		BCIR, //Boundry Circle
		BPOL, //Boundry Polygon
		BPLN, //Boundry Polyline
		FDIR, //Filter Direction
		FFRA, //Filter Fractal
		FHGT, //Filter Height
		FBIT, // Filter Bitmap
		FSLP, //Filter Slope
		ASCN, //Affector Shader Constant
		ACCN, //Affector Color Constant
		FSHD, //Filter Shader
		AHCN, //Affector Height Constant
		AHFR, //Affector Height Fractal
		AHFT, //Affector Height Terrace
		AFSC, //Affector Flora Static Collidable
		AFSN, //Affector Flora Static Non-Collidable
		AFDN, //Affector Flora Dynamic Near
		AFDF, //Affector Flora Dynamic Far
		AENV, //Environment
		AEXC, //Affector Exclude
		AROA, //Affector Road
		ARIV, //Affector River
		ACRF, //Affector Color Ramp Fractal
		ACRH, //Affector Color Ramp Height
		AHTR, //Affector Height Terrace
		ASRP, //Affector Shader Replace
}
