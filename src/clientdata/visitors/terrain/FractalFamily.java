package clientdata.visitors.terrain;

public class FractalFamily {

	private int fractal_id;
	private String fractal_label;
	
	private int seed;
	
	private int use_bias;
	private float bias;
	
	private int use_gain;
	private float gain;
	
	private int octaves;
	private float octaves_arg;
	
	private float amplitude;
	
	private float freq_x;
	private float freq_z;
	
	private float offset_x;
	private float offset_z;
	
	private int combination_type;
	
	private float offset;
	SWGRandom random;
	PerlinNoise noise;
	
	public int getFractal_id() {
		return fractal_id;
	}

	public void setFractal_id(int fractal_id) {
		this.fractal_id = fractal_id;
	}

	public String getFractal_label() {
		return fractal_label;
	}

	public void setFractal_label(String fractal_label) {
		this.fractal_label = fractal_label;
	}

	public int getUse_bias() {
		return use_bias;
	}

	public void setUse_bias(int use_bias) {
		this.use_bias = use_bias;
	}

	public float getBias() {
		return bias;
	}

	public void setBias(float bias) {
		this.bias = bias;
	}

	public int getUse_gain() {
		return use_gain;
	}

	public void setUse_gain(int use_gain) {
		this.use_gain = use_gain;
	}

	public float getGain() {
		return gain;
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public int getOctaves() {
		return octaves;
	}

	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}

	public float getOctaves_arg() {
		return octaves_arg;
	}

	public void setOctaves_arg(float octaves_arg) {
		this.octaves_arg = octaves_arg;
	}

	public float getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(float amplitude) {
		this.amplitude = amplitude;
		
		offset = 0.0f;
		float curr_amplitude = 0.0f;
		float next_amplitude = 1.0f;
		
		for(int i=0; i < octaves; ++i) {
			curr_amplitude += next_amplitude;
			next_amplitude *= amplitude;
		}
		
		offset = curr_amplitude;
		
		if(offset != 0) {
				offset = 1.0f/offset;
		}
	}

	public float getFreq_x() {
		return freq_x;
	}

	public void setFreq_x(float freq_x) {
		this.freq_x = freq_x;
	}

	public float getFreq_z() {
		return freq_z;
	}

	public void setFreq_z(float freq_z) {
		this.freq_z = freq_z;
	}

	public float getOffset_x() {
		return offset_x;
	}

	public void setOffset_x(float offset_x) {
		this.offset_x = offset_x;
	}

	public float getOffset_z() {
		return offset_z;
	}

	public void setOffset_y(float offset_z) {
		this.offset_z = offset_z;
	}

	public int getCombination_type() {
		return combination_type;
	}

	public void setCombination_type(int combination_type) {
		this.combination_type = combination_type;
	}

	public float getOffset() {
		return offset;
	}

	public void setOffset(float offset) {
		this.offset = offset;
	}

	public SWGRandom getRandom() {
		return random;
	}

	public void setRandom(SWGRandom random) {
		this.random = random;
	}

	public PerlinNoise getNoise() {
		return noise;
	}

	public void setNoise(PerlinNoise noise) {
		this.noise = noise;
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
		
		random = new SWGRandom();
		random.setSeed(seed);
		
		noise = new PerlinNoise(random);
		double coord[] = {0.0, 0.0};
		noise.noise2(coord);
	}
	
	public float getNoise(float x, float z) {
		float xFrequency = x * freq_x;
		float zFrequency = z * freq_z;
	
		double result = 0;
	
		switch (combination_type) 
		{
		case 0:
		case 1:
			result = calculateCombination1(xFrequency, zFrequency);
			break;
		case 2:
			result = calculateCombination2(xFrequency, zFrequency);
			break;
		case 3:
			result = calculateCombination3(xFrequency, zFrequency);
			break;
		case 4:
			result = calculateCombination4(xFrequency, zFrequency);
			break;
		case 5:
			result = calculateCombination5(xFrequency, zFrequency);
			break;
		}
	
		if (use_bias != 0) 
		{
			result = Math.pow(result, Math.log(bias) / Math.log(0.5));
		}
	
		if (use_gain != 0) 
		{
			if (result < 0.001) 
			{
				result = 0;
	
				return (float) result;
			}
	
			if (result > 0.999) 
			{
				result = 1.0;
	
				return (float) result;
			}
	
			double log_gain = Math.log(1.0 - gain) / Math.log(0.5);
	
			if (result < 0.5) 
			{
				result = Math.pow(result * 2, log_gain) * 0.5;
	
				return (float) result;
			}
	
			result = 1.0 - Math.pow((1.0 - result) * 2, log_gain) * 0.5;
		}
	
		return (float) result;
	}
	
	public double calculateCombination1(float x, float z) 
	{
		float curr_offset = 1, curr_ampl = 1;
		double result = 0;
	
		float zOffset = z + offset_z; 
		float xOffset = x + offset_x; 
		float zNoise, xNoise, noise_gen = 0;
		double coord[] = new double[2];
	
		for (int i = 0; i < octaves; ++i) 
		{
			zNoise = zOffset * curr_offset;
			xNoise = xOffset * curr_offset;
	
			coord[0] = xNoise;
			coord[1] = zNoise;
	
			noise_gen = noise.noise2(coord) * curr_ampl + noise_gen;
			curr_offset = curr_offset * octaves_arg; 
			curr_ampl = curr_ampl * amplitude; 
		}
	
		result = (noise_gen * offset + 1.0) * 0.5;
	
		return result;
	}
	
	public double calculateCombination2(float x, float z) 
	{
		float noise_gen = 0;
		float zOffset = z + offset_z;
		float zNoise, xOffset, xNoise;
		float curr_offset = 1.0f;
		float curr_ampl = 1.0f;
		double result = 0;
		double coord[] = new double[2];
	
		for (int i = 0; i < octaves; ++i) 
		{
			zNoise = zOffset * curr_offset;
			xOffset = x + offset_x;
			xNoise = xOffset * curr_offset;
	
			coord[0] = xNoise;
			coord[1] = zNoise;
	
			noise_gen = (float) ((1.0 - Math.abs(noise.noise2(coord))) * curr_ampl + noise_gen);
			curr_offset = curr_offset * octaves_arg; 
			curr_ampl = curr_ampl * amplitude;
		}
	
		result = noise_gen * offset;
	
		return result;
	}
	
	public double calculateCombination3(float x, float z) 
	{
		float curr_offset = 1.0f;
		float curr_ampl = 1.0f;
		double result = 0;
		float noise_gen = 0;
		float zOffset = z + offset_z; 
		float zNoise, xOffset, xNoise;
	
		double coord[]= new double[2];
	
		for (int i = 0; i < octaves; ++i) 
		{
			zNoise = zOffset * curr_offset;
			xOffset = x + offset_x; 
			xNoise = xOffset * curr_offset;
	
			coord[0] = xNoise;
			coord[1] = zNoise;
	
			noise_gen = Math.abs(noise.noise2(coord)) * curr_ampl + noise_gen;
			curr_offset = curr_offset * octaves_arg;
			curr_ampl = curr_ampl * amplitude; 
		}
	
		result = noise_gen * offset; 
	
		return result;
	}
	
	public double calculateCombination4(float x, float z) 
	{
		float noise_gen = 0;
		float zNoise, xNoise, noise_gain;
		float zOffset = z + offset_z; 
		float xOffset = x + offset_x; 
		double coord[] = new double[2];
	
		float curr_offset = 1.0f;
		float curr_ampl = 1.0f;
		double result = 0;
	
		for (int i = 0; i < octaves; ++i) 
		{
			zNoise = zOffset * curr_offset;
			xNoise = xOffset * curr_offset;
	
			coord[0] = xNoise;
			coord[1] = zNoise;
	
			noise_gain = noise.noise2(coord);
			if ( noise_gain >= 0.0 ) 
				if ( noise_gain > 1.0 )
					noise_gain = 1.0f;
			else 
				noise_gain = 0.0f;
	
			noise_gen = (float) ((1.0 - noise_gain) * curr_ampl + noise_gen);
			curr_offset = curr_offset * octaves_arg; 
			curr_ampl = curr_ampl * amplitude;
		}
	
		result = noise_gen * offset;
	
		return result;
	}
	
	public double calculateCombination5(float x, float z) 
	{
		float noise_gen = 0;
		float zNoise, xNoise, noise_gain;
		float zOffset = z + offset_z; 
		float xOffset = x + offset_x;
		double coord[] = new double[2];
		double result = 0;
	
		float curr_offset = 1.0f;
		float curr_ampl = 1.0f;
	
		for (int i = 0; i < octaves; ++i) 
		{
			zNoise = zOffset * curr_offset;
			xNoise = xOffset * curr_offset;
	
			coord[0] = xNoise;
			coord[1] = zNoise;
	
			noise_gain = noise.noise2(coord);
	
			if ( noise_gain >= 0.0 ) 
				if (noise_gain > 1.0)
					noise_gain = 1.0f;
			else 
				noise_gain = 0.0f;
	
			noise_gen = noise_gain * curr_ampl + noise_gen;
			curr_offset = curr_offset * octaves_arg;
			curr_ampl = curr_ampl * amplitude;
		}
	
		result = noise_gen * offset;
	
		return result;
	}
}
