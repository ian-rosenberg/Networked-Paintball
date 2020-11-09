public class Homework2 {
	public static void main(String[] args){
		int numArr[] = {1, 2, 3, 4, 5};
		
		for(int i = 0; i < numArr.length; i++)
		{
			System.out.println("Current Number " + numArr[i]);
		}
		
		for(int i = 0; i < numArr.length; i++)
		{
			if(numArr[i] % 2 == 0)
			{
				System.out.println("Even Number " + numArr[i]);
			}
		}
	}
}