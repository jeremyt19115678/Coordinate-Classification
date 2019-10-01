import java.util.Random;

public class PeasantNeuralNetwork {
	public double maxError = 0.001;
	public double maxEpoch = 100000;
	public double lr = .2;
	public Neuron[][] network;
	public double errorSum;
	public int epoch;

	// uses sigmoid
	public PeasantNeuralNetwork(int... neuronsPerLayer) {
		if (neuronsPerLayer.length >= 2) {
			int n_inputs = neuronsPerLayer[0];
			network = new Neuron[neuronsPerLayer.length - 1][];
			// System.out.println(network.length);
			for (int i = 0; i < network.length; i++) {
				network[i] = new Neuron[neuronsPerLayer[1 + i]];
				if (i == 0) {
					for (int j = 0; j < network[i].length; j++) {
						network[i][j] = new Neuron(n_inputs + 1);
					}
				} else {
					for (int j = 0; j < network[i].length; j++) {
						network[i][j] = new Neuron(network[i - 1].length + 1);
					}
				}
			}
		}
	}

	public double[] forwardPropagate(FakeDataSetRow row) {
		double[] input = new double[row.input.length + 1];
		input[input.length - 1] = 1;
		for (int i = 0; i < input.length - 1; i++)
			input[i] = row.input[i];
		for (int i = 0; i < network.length; i++) {
			double[] nextLInput = new double[network[i].length + 1];
			// bias
			nextLInput[nextLInput.length - 1] = 1;
			for (int j = 0; j < network[i].length; j++) {
				nextLInput[j] = network[i][j].transfer(input);
			}
			input = nextLInput;
		}
		return input;
	}

	public double sigmoidDer(double i) {
		return i * (1 - i);
	}

	public void backPropagate(FakeDataSetRow row) {
		double[] target = row.target;
		for (int i = network.length - 1; i >= 0; i--) {
			// output layer
			if (i == network.length - 1) {
				for (int j = 0; j < target.length; j++) {
					network[i][j].errorSignal = target[j] - network[i][j].output;
				}
				// hidden layer
			} else {
				for (int j = 0; j < network[i].length; j++) {
					double error = 0;
					for (int k = 0; k < network[i + 1].length; k++) {
						error += network[i + 1][k].errorSignal * network[i + 1][k].weights[j];
					}
					network[i][j].errorSignal = error;
				}
			}
		}
	}

	public void updateWeights(FakeDataSetRow row) {
		double[] input = new double[0];
		for (int i = 0; i < network.length; i++) {
			// input layer
			if (i == 0) {
				input = new double[row.input.length + 1];
				input[input.length - 1] = 1;
				for (int j = 0; j < input.length - 1; j++)
					input[j] = row.input[j];
			}
			for (int j = 0; j < network[i].length; j++) {
				for (int k = 0; k < network[i][j].weights.length; k++) {
					network[i][j].weights[k] += lr * network[i][j].errorSignal * sigmoidDer(network[i][j].output)
							* input[k];
				}
			}
			input = new double[network[i].length + 1];
			input[input.length - 1] = 1;
			for (int j = 0; j < input.length - 1; j++)
				input[j] = network[i][j].output;
		}
	}

	public void learn(FakeDataSetRow[] set) {
		for (int i = 1; i <= maxEpoch; i++) {
			errorSum = 0;
			shuffleSet(set);
			for (FakeDataSetRow row : set) {
				double[] outputs = forwardPropagate(row);
				double[] targets = new double[row.target.length + 1];
				for (int j = 0; j < row.target.length; j++) {
					targets[j] = row.target[j];
				}
				targets[targets.length - 1] = 1;
				for (int j = 0; j < targets.length; j++) {
					errorSum += Math.pow(targets[j] - outputs[j], 2);
				}
				backPropagate(row);
				updateWeights(row);
			}
			epoch = i;
			if (i % 5000 == 0)
				System.out.println("Epoch:: " + epoch + " Error:: " + errorSum);
			if (errorSum < maxError)
				break;
		}
		System.out.println("Epoch:: " + epoch + " Error:: " + errorSum);
	}

	static void shuffleSet(FakeDataSetRow[] set) {
		Random r = new Random();
		for (int i = set.length - 1; i > 0; i--) {
			int index = r.nextInt(i + 1);
			// Simple swap
			FakeDataSetRow a = set[index];
			set[index] = set[i];
			set[i] = a;
		}
	}

	public String toString() {
		String ans = "";
		ans += "The network has " + network.length + " layers\n";
		for (int i = 0; i < network.length; i++) {
			ans += "Layer No. " + (i + 1) + " has " + network[i].length + " neurons.\n";
			for (int j = 0; j < network[i].length; j++) {
				ans += "Neuron " + j + " has " + network[i][j].getSize() + " weights.\n";
				ans += network[i][j].toString() + "\n";
			}
		}
		return ans;
	}
}