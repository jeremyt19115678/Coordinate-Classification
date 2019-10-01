import java.awt.Font;
import java.util.ArrayList;

public class Runner {
	private final static int width = 800;
	private final static int height = 800;
	private static boolean populated = false;
	private static int hiddenN = 9;
	private static PeasantNeuralNetwork myMlPerceptron = new PeasantNeuralNetwork(2, hiddenN, 1);
	private static FakeDataSetRow[] trainingSet = new FakeDataSetRow[0];
	private static ArrayList<Point> PointList = new ArrayList<Point>();
	private static ArrayList<Integer> topBottom = new ArrayList<Integer>();
	private static boolean learned = false;
	private static String[] instructions = { "Press M to shift type of points populating",
			"Press P to Populate the screen with data points", "Press C to Clear the screen.",
			"Press C again to clear training set", "Press L to make the perceptron learn.",
			"Press I to Increase the amount of hidden layer neurons.",
			"Press D to Decrease the amount of hidden layer neurons.",
			"Current # of Hidden layer neurons = " + hiddenN , "You are currently adding BLACK data dots"};
	private static boolean topPoints = true;

	public static void main(String[] args) {

		// setup for graphics
		StdDraw.setCanvasSize(width, height);
		StdDraw.setYscale(-height / 2, height / 2);
		StdDraw.setXscale(-width / 2, width / 2);
		StdDraw.enableDoubleBuffering();
		drawAxis();
		displayInfo();
		StdDraw.show();

		while (true) {
			if (StdDraw.hasNextKeyTyped()) {
				char input = StdDraw.nextKeyTyped();
				if (input == 'r' || input == 'R') {
					if (learned) {
						input = 'l';
						learned = false;
					}
				}
				if (input == 'p' || input == 'P') {
					if (learned)
						populate(myMlPerceptron, trainingSet);
				} else if (input == 'C' || input == 'c') {
					if (populated) {
						clearScreen();
						if (learned)
							classifyAndShow(myMlPerceptron, trainingSet);
						StdDraw.show();
						populated = false;
					} else {
						trainingSet = new FakeDataSetRow[0];
						PointList.clear();
						topBottom.clear();
						learned = false;
						topPoints = true;
						clearScreen();
						StdDraw.show();
					}
				} else if (input == 'l' || input == 'L') {
					// learn the training set
					if (!learned) {
						myMlPerceptron = new PeasantNeuralNetwork(2, hiddenN, 1);
						System.out.println("Learning... ");
						trainingSet = new FakeDataSetRow[PointList.size()];
						for (int i = 0; i < trainingSet.length; i++) {
							trainingSet[i] = new FakeDataSetRow(
									new double[] { PointList.get(i).x / width * 2, PointList.get(i).y / width * 2 },
									new double[] { topBottom.get(i) });
						}
						clearScreen();
						// set all the samples to Magenta to indicate it's learning
						for (FakeDataSetRow dataRow : trainingSet) {
							// visualizing inputs and results
							double[] coordinates = dataRow.input;
							double x = coordinates[0] * width / 2;
							double y = coordinates[1] * width / 2;
							Point p = new Point(x, y);
							p.draw(StdDraw.MAGENTA);
						}
						// learning in progress
						StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.filledRectangle(0, 0, 200, 100);
						StdDraw.setPenColor(StdDraw.BLACK);
						StdDraw.rectangle(0, 0, 200, 100);
						Font font = new Font("Comic Sans MS", Font.BOLD, 24);
						StdDraw.setFont(font);
						StdDraw.setPenColor(StdDraw.RED);
						StdDraw.text(0, 0, "LEARNING IN PROGRESS");
						StdDraw.show();
						// actually learn
						myMlPerceptron.learn(trainingSet);
						learned = true;
						System.out.println("done.");
						StdDraw.clear();
						drawAxis();
						classifyAndShow(myMlPerceptron, trainingSet);
						displayInfo();
						StdDraw.show();
					}
				} else if (input == 'm' || input == 'M') {
					if (topPoints) {
						topPoints = false;
					}else {
						topPoints = true;
					}
					updateInfo();
				} else if (input == 'i' || input == 'I') {
					if (hiddenN < 50) {
						hiddenN++;
					}
					learned = false;
					updateInfo();
				} else if (input == 'd' || input == 'D') {
					if (hiddenN >1) {
						hiddenN--;
					}
					learned = false;
					updateInfo();
				}
			}
			if (StdDraw.mousePressed()) { // handle all mouse inputs
				if (!learned) {
					Point pt = new Point(StdDraw.mouseX(), StdDraw.mouseY());
					PointList.add(pt);
					if (topPoints) {
						pt.draw();
						topBottom.add(1);
					} else {
						pt.draw(StdDraw.PRINCETON_ORANGE);
						topBottom.add(0);
					}
					StdDraw.show();
				} else {
					updateInfo();
					learned = false;
					topPoints = true;
				}
			}
		}
	}

	public static void updateInfo() {
		clearScreen();
		for (int i = 0; i < PointList.size(); i++) {
			PointList.get(i).top = false;
			PointList.get(i).neutral = true;
			PointList.get(i).sample = true;
			if (topBottom.get(i) == 0)
				PointList.get(i).draw(StdDraw.PRINCETON_ORANGE);
			else
				PointList.get(i).draw();
		}
		StdDraw.show();
	}
	
	public static void clearScreen() {
		StdDraw.clear();
		displayInfo();
		drawAxis();
		StdDraw.show();
	}

	public static void populate(PeasantNeuralNetwork nnet, FakeDataSetRow[] trainingSet) {
		double startX = -width / 2;
		double startY = -height / 2;
		for (int i = 0; i < (int) (width / 8) + 1; i++) {
			for (int j = 0; j < (int) (height / 8) + 1; j++) {
				double x = (startX + i * 8);
				double y = (startY + j * 8);
				Point p = new Point(x, y);
				p.sample = false;
				FakeDataSetRow dataPoint = new FakeDataSetRow(new double[] { x / width * 2, y / height * 2 },
						new double[] { 0 });
				double[] nnetOutput = nnet.forwardPropagate(dataPoint);
				if (nnetOutput[0] > .5)
					p.setTop();
				else
					p.neutral = false;
				p.draw();
			}
		}
		StdDraw.show();
		drawAxis();
		classifyAndShow(nnet, trainingSet);
		displayInfo();
		populated = true;
	}

	public static void classifyAndShow(PeasantNeuralNetwork nnet, FakeDataSetRow[] testSet) {
		for (FakeDataSetRow dataRow : testSet) {
			// visualizing inputs and results
			double[] coordinates = dataRow.input;
			double x = coordinates[0] * width / 2;
			double y = coordinates[1] * height / 2;
			Point p = new Point(x, y);
			double[] networkOutput = nnet.forwardPropagate(dataRow);
			if (networkOutput[0] > .5)
				p.setTop();
			else
				p.neutral = false;
			p.draw();
		}
	}

	private static void drawAxis() {
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.line(-width / 2, 0, width / 2, 0);
		StdDraw.line(0, height / 2, 0, -height / 2);
	}

	public static void displayInfo() {
		if (!learned)
			instructions = new String[] { "Press M to shift type of points populating",
					"Press P to Populate the screen with data points", "Press C to Clear the screen.",
					"Press C again to clear training set", "Press L to make the perceptron learn.",
					"Press I to Increase the amount of hidden layer neurons.",
					"Press D to Decrease the amount of hidden layer neurons.",
					"Current # of Hidden layer neurons = " + hiddenN , "You are currently adding BLACK data dots" };
		else
			instructions = new String[] { "Press M to shift type of points populating",
					"Press P to Populate the screen with data points", "Press C to Clear the screen.",
					"Press C again to clear training set", "Press L to make the perceptron learn.",
					"Press I to Increase the amount of hidden layer neurons.",
					"Press D to Decrease the amount of hidden layer neurons.",
					"Current # of Hidden layer neurons = " + hiddenN , "You are currently adding BLACK data dots", "", "Current Error = " + myMlPerceptron.errorSum,
					"Epochs taken = " + myMlPerceptron.epoch };
		if (!topPoints) {
			instructions[8] = "You are currently adding ORANGE dots";
		}else {
			instructions[8] = "You are currently adding BLACK dots";
		}
		StdDraw.setPenColor(StdDraw.BLACK);
		Font font = new Font("Comic Sans MS", Font.PLAIN, 12);
		StdDraw.setFont(font);
		for (int i = 0; i < instructions.length; i++) {
			StdDraw.text(-width / 2 + 160, height / 2 - 20 - 14 * i, instructions[i]);
		}
		StdDraw.show();

	}
}
