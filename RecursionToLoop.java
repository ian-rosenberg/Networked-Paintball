import java.util.Scanner;

public class RecursionToLoop {
  
	public static int sum(int num) {
		if (num > 0) {
			System.out.println("Sum current val: " + num);
			return num + sum(num - 1);
		}
		return 0;
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter a number to sum: ");
		
		int num = input.nextInt();
		int comp = sum(num);
		int accum = 0;
		
		System.out.println("Current val: " + num);
		
		for(int i = num; i > 0; i--)
		{
			accum += (i-1);
			
			System.out.println("Current val: " + num);
		}
		
	}
}