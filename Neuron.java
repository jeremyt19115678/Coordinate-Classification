import java.util.Arrays;

public class Neuron {
	
	public double[] weights;
	public double errorSignal;
	public double output;
	
	public Neuron (int length) {
		weights = new double[length];
		for (int i = 0; i < weights.length; i++)
			weights[i] = Math.random();
		errorSignal = 0;
	}
	
	public double activate(double[] inputs) {
		double ans= 0;
		for (int i = 0; i < weights.length; i++)
			ans += weights[i]*inputs[i];
		return ans;
	}
	
	public double transfer(double[] inputs) {
		double activation = activate(inputs);
		output =  1/(1+Math.exp(-activation));
		return output;
	}
	
	public int getSize() {
		return weights.length;
	}
	
	public String toString() {
		return Arrays.toString(weights);
	}
}
