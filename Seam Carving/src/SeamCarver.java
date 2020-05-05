import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

public class SeamCarver {
	
	private char[][] red, green, blue;
	private int height, width;

	// create a seam carver object based on the given picture
	public SeamCarver(Picture picture)
	{
		if (picture == null)
			throw new IllegalArgumentException("Null Argument");

		this.height = picture.height();
		this.width = picture.width();
		this.red = new char[height][width];
		this.green = new char[height][width];
		this.blue = new char[height][width];

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Color color = picture.get(j, i);
				red[i][j] = (char) color.getRed();
				green[i][j] = (char) color.getGreen();
				blue[i][j] = (char) color.getBlue();
			}
		}

	}

	// current picture
	public Picture picture()
	{
		Picture result = new Picture(width, height);
		
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Color color = new Color(red[i][j], green[i][j], blue[i][j]);
				result.set(j, i, color);
			}
		}
		return result;
	}

	// width of current picture
	public int width()
	{
		return this.width;
	}

	// height of current picture
	public int height()
	{
		return this.height;
	}

	// energy of pixel at column x and row y
	public double energy(int x, int y)
	{
		if (x < 0 || y < 0 || x >= width || y >=height)
			throw new IllegalArgumentException("Out of boundary ");
		
		if (y == 0 || y == height - 1 || x == 0 || x == width - 1)
		{
			return 1000;
		}
		
		int Rx = red[y][x - 1] - red[y][x + 1];
		int Gx = green[y][x - 1] - green[y][x + 1];
		int Bx = blue[y][x - 1] - blue[y][x + 1];
		double deltaX = Rx * Rx + Gx * Gx + Bx * Bx;
		
		int Ry = red[y - 1][x] - red[y + 1][x];
		int Gy = green[y - 1][x] - green[y + 1][x];
		int By = blue[y - 1][x] - blue[y + 1][x];
		double deltaY = Ry * Ry + Gy * Gy + By * By;
		
		return  Math.sqrt(deltaX + deltaY);
	}

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam()
	{
		double[][] distTo = new double[height][width];
		int[][] edgeTo = new int[height][width];
		
		for (int i = 0; i < height; i++)
		{
			distTo[i][0] = 1000;
			edgeTo[i][0] = i;
		}
		
		for (int j = 1; j < width; j++)
		{
			for (int i = 0; i < height ;i++)
			{
				double up,left,down;
				if (i == 0)
					up = Double.POSITIVE_INFINITY;
				else
					up = distTo[i - 1][j - 1];
				
				left = distTo[i][j - 1];
				
				if (i == height - 1)
					down = Double.POSITIVE_INFINITY;
				else
					down = distTo[i + 1][j - 1];
				
				double min = Math.min(Math.min(up, left), down);
				
				distTo[i][j] = min + energy(j,i);
				
				if (min == up)
					edgeTo[i][j] = i - 1;
				else if (min == left)
					edgeTo[i][j] = i;
				else
					edgeTo[i][j] = i + 1;
			}
		}
		
		int min = 0;
		for (int i = 1; i < height; i++)
		{
			if (distTo[i][width - 1] < distTo[min][width - 1])
				min = i;
		}
		
		Stack<Integer> reversePath = new Stack<Integer>();
		reversePath.push(min);
		for (int j = width - 1; j > 0 ; j--)
		{
			reversePath.push(edgeTo[min][j]);
			min = edgeTo[min][j];
		}
		
		int[] path = new int[width];
		for (int i = 0; i< width; i++)
			path[i] = reversePath.pop();
		
		return path;
	}

	// sequence of indices for vertical seam
	public int[] findVerticalSeam()
	{
		double[][] distTo = new double[height][width];
		int[][] edgeTo = new int[height][width];
		
		for (int j = 0; j < width; j++)
		{
			distTo[0][j] = 1000;
			edgeTo[0][j] = j;
		}
		for (int i = 1; i < height; i++)
		{
			for (int j  = 0 ; j < width ; j++)
			{
				double left,up,right;
				if (j == 0)
					left = Double.POSITIVE_INFINITY;
				else
					left = distTo[i - 1][j - 1];
				
				up = distTo[i - 1][j];
				if (j == width - 1)
					right = Double.POSITIVE_INFINITY;
				else
					right = distTo[i - 1][j +1];
				
				double min = Math.min(Math.min(left, up), right);
				
				distTo[i][j] = min + energy(j,i);
				
				if (min == left)
					edgeTo[i][j] = j - 1;
				else if (min == up)
					edgeTo[i][j] = j;
				else
					edgeTo[i][j] = j + 1;
			}
		}
		
		int min = 0;
		for (int j = 1; j < width; j++)
		{
			if (distTo[height - 1][j] < distTo[height - 1][min])
				min = j;
		}
		
		Stack<Integer> reversePath = new Stack<Integer>();
		reversePath.push(min);
		for (int i = height - 1; i > 0 ;i--)
		{
			reversePath.push(edgeTo[i][min]);
			min = edgeTo[i][min];
		}
		
		int[] path = new int[height];
		for (int i = 0; i< height; i++)
			path[i] = reversePath.pop();
		
		return path;
	}

	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] seam)
	{
		if (height <= 1)
			throw new IllegalArgumentException("Can't remove seam");
		if (seam == null)
			throw new IllegalArgumentException("Null Argument");
		if (seam.length != width)
			throw new IllegalArgumentException("Invalid Array Length");
		for (int i = 0; i < width; i++)
		{
			if (seam[i] < 0 || seam[i] >= height)
				throw new IllegalArgumentException("Out of boundary array element");
		}
		for (int i =0; i < width - 1; i++)
		{
			if (Math.abs(seam[i] - seam[i+1]) > 1)
				throw new IllegalArgumentException("Invalid Seam Elements");
		}
		
		for (int j = 0; j < width; j++)
		{
			for (int i = seam[j]; i < height - 1; i++)
			{
				red[i][j] = red[i + 1][j];
				green[i][j] = green[i + 1][j];
				blue[i][j] = blue[i + 1][j];
			}
		}

		height--;
		return;
	}

	// remove vertical seam from current picture
	public void removeVerticalSeam(int[] seam)
	{
		if (width <= 1)
			throw new IllegalArgumentException("Can't remove seam");
		if (seam == null)
			throw new IllegalArgumentException("Null Argument");
		if (seam.length != height)
			throw new IllegalArgumentException("Invalid Array Length");
		for (int i = 0; i < height; i++)
		{
			if (seam[i] < 0 || seam[i] >= width)
				throw new IllegalArgumentException("Out of boundary array element");
		}
		for (int i =0; i < height - 1; i++)
		{
			if (Math.abs(seam[i] - seam[i+1]) > 1)
				throw new IllegalArgumentException("Invalid Seam Elements");
		}
		
		for (int i = 0; i < height; i++)
		{
			for (int j = seam[i]; j < width - 1; j++)
			{
				red[i][j] = red[i][j + 1];
				green[i][j] = green[i][j + 1];
				blue[i][j] = blue[i][j + 1];
			}
		}

		width--;
		return;
	}

	//  unit testing (optional)
	public static void main(String[] args)
	{

		Picture hello = new Picture("C:\\Users\\Youssef\\Desktop\\College\\Courses\\Algorithms 2\\Assignments\\seam\\Specs\\6x5.png");
		SeamCarver test = new SeamCarver(hello);
		
		/*
		System.out.println(test.height());
		System.out.println(test.width());
		int[] result = test.findHorizontalSeam();
		for (int i = 0; i < result.length; i++)
			System.out.print(result[i]+" - ");
		test.removeHorizontalSeam(result);
		
		System.out.println();
		
		result = test.findHorizontalSeam();
		for (int i = 0; i < result.length; i++)
			System.out.print(result[i]+" - ");
			*/
		
		System.out.println("Before removing");
		System.out.println();
		for (int i = 0; i < test.height() ; i++)
		{
			for (int j = 0 ; j < test.width() ; j++)
			{
				System.out.print(test.picture().getRGB(j, i)+ " ");
			}
			System.out.println();
		}
		
		int[] verticalSeam = {2,3,3,2,1};
		test.removeVerticalSeam(verticalSeam);
		
		System.out.println("After removing");
		System.out.println();
		for (int i = 0; i < test.height() ; i++)
		{
			for (int j = 0 ; j < test.width() ; j++)
			{
				System.out.print(test.picture().getRGB(j, i)+ " ");
			}
			System.out.println();
		}
		
	}
	
	
}