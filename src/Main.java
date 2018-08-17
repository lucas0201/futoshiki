import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		int n;
		int heuristicFlag;
		
		heuristicFlag = 2;
		Futoshiki futoshiki = new Futoshiki();
		n = Input.sc.nextInt();
		
		for(int i=0; i<n; i++) {
			futoshiki.read();
			System.out.println(i+1);
			try {
				futoshiki.solve(heuristicFlag);
			} catch (FutoshikiException e) {
				System.out.println(e);
				continue;
			}
			System.out.println(futoshiki);
		}
	}

}