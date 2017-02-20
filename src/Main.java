public class Main {

	
	public static void main(String [ ] args)
	{
		int n = 4;
		
		if(args.length == 1){
			n=Integer.parseInt(args[0]);
		}else{
			System.out.println("No has escollit el número de servidors, s'ha establert el valor per defecte");
		}
		
		System.out.println("Número de servidors: "+n);
		
		Servidor [] serverList = new Servidor[n];
		
		for (int i=1 ; i<=n ; i++){
			serverList[i-1] = new Servidor (i ,i % 3 == 0 ? true : false, 0, n );
		}
		
		for (int i=0; i<n ; i++){
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			serverList[i].start();
		}
		
	}
}
