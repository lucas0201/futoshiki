import java.io.IOException;
import java.util.Arrays;

/**
 * Classe que implementa um tabuleiro de futoshiki e metodos de resolucao.
 *
 */
public class Futoshiki {
	private int d;						//Dimensao
	private int[][][] board;			//Tabuleiro
	private int assignments;			//Numero de atribuicoes numa solucao
	
	/**
	 * Dimensao.
	 * @return		d
	 */
	public int getD() {
		return d;
	}
	
	/**
	 * Numero de atribuicoes feitas numa tentativa de solucao.
	 * @return		assignments
	 */
	public int getAssignments() {
		return assignments;
	}

	/**
	 * Metodo que verifica quais sao os valores possiveis para uma variavel.
	 * @param i		linha da variavel
	 * @param j		coluna da variavel
	 * @return		vetor em que a possibilidade do valor i esta indicada na posicao de indice i
	 */
	private boolean[] values(int i, int j) {
		boolean[] value = new boolean[d];
		
		if(board[i][j][0] >= 0) {			//Posicao ocupada
			Arrays.fill(value, false);		//Somente seu proprio valor e possivel
			value[board[i][j][0]] = true;
			return value;
		}
		
		Arrays.fill(value, true);
		
		//Valores iguais na linha e na coluna
		for(int k=0; k<d; k++) {
			if(board[i][k][0] >= 0)
				value[board[i][k][0]] = false;
			if(board[k][j][0] >= 0)
				value[board[k][j][0]] = false;
		}
		
		//Restricoes
		for(int l=1; l<board[i][j].length; l++) {
			switch(board[i][j][l]) {
			
			//Maior que o valor acima
			case 0:	if(i>0 && board[i-1][j][0] >= 0)
						for(int k=0; k<=board[i-1][j][0]; k++)
							value[k] = false;
					break;
			//Menor que o valor acima
			case 1:	if(i>0 && board[i-1][j][0] >= 0)
						for(int k=board[i-1][j][0]; k<d; k++)
								value[k] = false;
					break;
			//Maior que o valor a direita
			case 2:	if(j+1<d && board[i][j+1][0] >= 0)
						for(int k=0; k<=board[i][j+1][0]; k++)
							value[k] = false;
					break;
			//Menor que o valor a direita
			case 3:	if(j+1<d && board[i][j+1][0] >= 0)
						for(int k=board[i][j+1][0]; k<d; k++)
							value[k] = false;
					break;
			//Maior que o valor abaixo
			case 4:	if(i+1<d && board[i+1][j][0] >= 0)
						for(int k=0; k<=board[i+1][j][0]; k++)
							value[k] = false;
					break;
			//Menor que o valor abaixo
			case 5:	if(i+1<d && board[i+1][j][0] >= 0)
						for(int k=board[i+1][j][0]; k<d; k++)
							value[k] = false;
					break;
			//Maior que o valor a esquerda
			case 6:	if(j>0 && board[i][j-1][0] >= 0)
						for(int k=0; k<=board[i][j-1][0]; k++)
							value[k] = false;
					break;
			//Menor que o valor a esquerda
			case 7:	if(j>0 && board[i][j-1][0] >= 0)
						for(int k=board[i][j-1][0]; k<d; k++)
							value[k] = false;
					break;
			}
		}
		
		return value;
	
	}
	
	/**
	 * Busca a variavel mais restrita.
	 * @return		vetor com a linha, coluna e o numero de valores possiveis, respectivamente
	 */
	private int[] mrv(ForwardChecking fc) {
		int[] mrv = new int[3];
		
		mrv[2] = d+1;
		int n;
		for(int i=0; i<d; i++) {
			for(int j=0; j<d; j++) {
				n = fc.countValues(i, j);
				if(!(n==1 && board[i][j][0] >= 0) && mrv[2] > n) {	//Verifica se o numero e menor que o minimo
					mrv[0] = i;
					mrv[1] = j;
					mrv[2] = n;
				}
			}
		}
		
		return mrv;
	}
	
	/**
	 * Resolve o futoshiki por backtracking.
	 * @param i						linha inicial (menor linha com alguma variavel livre)
	 * @param j						coluna inicial (menor coluna com alguma variavel livre)
	 * @return						true se o futoshiki foi resolvido, false caso nao haja solucao
	 * @throws FutoshikiException	caso o numero de atribuicoes tenha atingido um numero excessivo
	 */
	private boolean backtracking(int i, int j) throws FutoshikiException {
		if(assignments > 1000000)
			throw new FutoshikiException("Numero de atribuicoes excede limite maximo");
		
		//Busca de posicoes livres
		
		if(i==d)			//Sem posicoes livres
			return true;
		
		while(j<d && board[i][j][0] >= 0)	//Busca na linha i
			j++;
		
		if(j==d) {								//Busca no resto do tabuleiro
			for(i++; i<d; i++) {
				for(j=0; j<d && board[i][j][0] >= 0; j++);
				if(j<d)
					break;
			}
		}
		if(i==d)			//Sem posicoes livres
			return true;
		
		//Calculo das proximas linha e coluna
		
		int nextI, nextJ;
		
		if(j == d - 1) {
			nextI = i + 1;
			nextJ = 0;
		}
		else {
			nextI = i;
			nextJ = j + 1;
		}
		
		//Atribuicoes dos valores possiveis
		
		boolean[] value = values(i, j);	
		for(int k=0; k<d; k++) {
			if(value[k]) {
				board[i][j][0] = k;
				assignments++;
				try {
					if(backtracking(nextI, nextJ))
						return true;
				} catch(FutoshikiException e) {
					board[i][j][0] = -1;
					throw e;
				}
			}
		}
		board[i][j][0] = -1;	//Volta
		
		return false;
	}
	
	/**
	 * Resolve o futoshiki por backtracking com Forward Checking.
	 * @param i						linha inicial (menor linha com alguma variavel livre)
	 * @param j						coluna inicial (menor coluna com alguma variavel livre)
	 * @param fc					matriz de Forward Checking
	 * @return						true se o futoshiki foi resolvido, false caso nao haja solucao
	 * @throws FutoshikiException	caso o numero de atribuicoes tenha atingido um numero excessivo
	 */
	private boolean backtrackingFC(int i, int j, ForwardChecking fc) throws FutoshikiException {
		if(assignments > 1000000)
			throw new FutoshikiException("Numero de atribuicoes excede limite maximo");
		
		int[] mrv = mrv(fc);
		if(mrv[2] == 0)		//Variavel com nenhum valor possivel
			return false;
		if(mrv[2] > d)		//Nenhuma variavel livre
			return true;
		
		//Busca de posicoes livres
		
		while(j<d && board[i][j][0] >= 0)	//Busca na linha i
			j++;
		
		if(j==d) {								//Busca no resto do tabuleiro
			for(i++; i<d; i++) {
				for(j=0; j<d && board[i][j][0] >= 0; j++);
				if(j<d)
					break;
			}
		}
		
		//Calculo das proximas linha e coluna
		
		int nextI, nextJ;
		
		if(j == d - 1) {
			nextI = i + 1;
			nextJ = 0;
		}
		else {
			nextI = i;
			nextJ = j + 1;
		}
		
		//Atribuicoes dos valores possiveis
		
		boolean[] value = fc.getValues(i, j);
		for(int k=0; k<d; k++) {
			if(value[k]) {
				board[i][j][0] = k;
				assignments++;
				try {
					if(backtrackingFC(nextI, nextJ, new ForwardChecking(i, j, k, fc)))
						return true;
				} catch(FutoshikiException e) {
					board[i][j][0] = -1;
					throw e;
				}
			}
		}
		board[i][j][0] = -1;	//Volta
		
		return false;
		
	}

	/**
	 * Resolve o futoshiki por backtracking com Forward Checking e MRV.
	 * @param fc					matriz de Forward Checking
	 * @return						true se o futoshiki foi resolvido, false caso nao haja solucao
	 * @throws FutoshikiException	caso o numero de atribuicoes tenha atingido um numero excessivo
	 */
	private boolean backtrackingFCMRV(ForwardChecking fc) throws FutoshikiException {
		if(assignments > 1000000)
			throw new FutoshikiException("Numero de atribuicoes excede limite maximo");
		
		int[] mrv = mrv(fc);
		if(mrv[2] == 0)		//Variavel com nenhum valor possivel
			return false;
		if(mrv[2] > d)		//Nenhuma variavel livre
			return true;
		
		//Atribuicoes
		
		for(int k=0; k<d; k++) {
			if(fc.getValue(mrv[0], mrv[1], k)) {
				board[mrv[0]][mrv[1]][0] = k;
				assignments++;
				try {
					if(backtrackingFCMRV(new ForwardChecking(mrv[0], mrv[1], k, fc)))
						return true;
				} catch(FutoshikiException e) {
					board[mrv[0]][mrv[1]][0] = -1;
					throw e;
				}
			}
		}
		board[mrv[0]][mrv[1]][0] = -1;	//Volta
		
		return false;
		
	}

	/**
	 * Efetua a leitura dos dados de um futoshiki pela entrada padrao.
	 * @throws IOException
	 */
	public void read() throws IOException {
		d = Input.sc.nextInt();	// Le a dimensao do tabuleiro
		int r = Input.sc.nextInt();		// Le o numero de restricoes
		assignments = 0;
		board = new int[d][d][1];
			
		//Leitura dos valores das variaveis
		for(int i=0; i<d; i++)
			for(int j=0; j<d; j++)
				board[i][j][0] = Input.sc.nextInt()-1;
		
		//Leitura das restricoes
		int x1, y1, x2, y2;
		for(int i=0; i<r; i++) {
			
			//Leitura
			y1 = Input.sc.nextInt()-1;
			x1 = Input.sc.nextInt()-1;
			y2 = Input.sc.nextInt()-1;
			x2 = Input.sc.nextInt()-1;
			
			
			//Realocacao dos vetores para a nova restricao
			
			int[] tmp1 = board[y1][x1];
			board[y1][x1] = new int[tmp1.length+1];
			for(int j=0; j<tmp1.length; j++)
				board[y1][x1][j] = tmp1[j];
			
			int[] tmp2 = board[y2][x2];
			board[y2][x2] = new int[tmp2.length+1];
			for(int j=0; j<tmp2.length; j++)
				board[y2][x2][j] = tmp2[j];

			//Atribuicao restricao correspondente
			
			if(y1 < y2) {//(x2, y2) esta abaixo de (x1, y1)
				board[y1][x1][tmp1.length] = 5;//Menor que o valor abaixo
				board[y2][x2][tmp2.length] = 0;//Maior que o valor acima
			} else if(y1 > y2) {//(x2, y2) esta acima de (x1, y1)
				board[y1][x1][tmp1.length] = 1;//Menor que o valor acima
				board[y2][x2][tmp2.length] = 4;//Maior que o valor abaixo
			} else if(x1 < x2) {//(x2, y2) esta a direita de (x1, y1)
				board[y1][x1][tmp1.length] = 3;//Menor que o valor a direita
				board[y2][x2][tmp2.length] = 6;//Maior que o valor a esquerda
			} else if(x1 > x2) {//(x2, y2) esta a esquerda de (x1, y1)
				board[y1][x1][tmp1.length] = 7;//Menor que o valor a esquerda
				board[y2][x2][tmp2.length] = 2;//Maior que o valor a direita
			}
		}
			
	}
	
	/**
	 * Soluciona o futoshiki.
	 * @param heuristicFlag			forma de resolucao: 0 - backtracking, 1 - backtracking com FC, 2 - backtracking com FC e MRV
	 * @return						true se o futoshiki foi resolvido, false caso nao haja solucao
	 * @throws FutoshikiException
	 */
	public boolean solve(int heuristicFlag) throws FutoshikiException {
		switch(heuristicFlag) {
			case 0:	assignments = 0;
					return backtracking(0, 0);
				
			case 1:	assignments = 0;
					return backtrackingFC(0, 0, new ForwardChecking());
				
			case 2:	assignments = 0;
					return backtrackingFCMRV(new ForwardChecking());
		}
		
		return false;
		
	}
	
	/**
	 * Retorna uma string com os valores de cada variavel na forma de uma matriz.
	 */
	@Override
	public String toString() {
		String s = "";
		for(int i=0; i<d; i++) {
			for(int j=0; j<d-1; j++)
				s += board[i][j][0]+1 + " ";
			s += board[i][d-1][0]+1 + "\n";
		}
		return s.substring(0, s.length()-1);
		
	}
	
	public Futoshiki() {
		d = 0;
		board = null;
		assignments = 0;
	}
	
	/**
	 * Classe de verificacao adiante, referente aos valores possiveis para cada variavel.
	 *
	 */
	private class ForwardChecking {
		private boolean[] fc;			//Vetor de possibilidades
		
		/**
		 * Possibilidade de um valor numa variavel.
		 * @param i			linha da variavel
		 * @param j			coluna da variavel
		 * @param v			valor
		 * @return			true se o valor e possivel, false caso contrario
		 */
		public boolean getValue(int i, int j, int v) {
			return fc[i*d*d+j*d+v];
		}
		
		/**
		 * Copia o vetor de possibilidades.
		 * @return			possibilidades de todas as variaveis.
		 */
		public boolean[] getValues() {
			return Arrays.copyOf(fc, fc.length);
		}
		
		/**
		 * Valores possiveis para uma variavel.
		 * @param i			linha da variavel
		 * @param j			coluna da variavel
		 * @return			vetor de possibilidades
		 */
		public boolean[] getValues(int i, int j) {
			i = i*d*d+j*d;
			return Arrays.copyOfRange(fc, i, i+d);
		}
		
		/**
		 * Atribui true ou false para uma determinada possibilidade de uma variavel
		 * @param i			linha da variavel
		 * @param j			coluna da variavel
		 * @param v			valor
		 * @param b			true ou false
		 */
		private void setValue(int i, int j, int v, boolean b) {
			fc[i*d*d+j*d+v] = b;
		}
		
		/**
		 * Atribui true ou false para as possibilidades de uma variavel de acordo com um vetor de possibilidades.
		 * @param i			linha da variavel
		 * @param j			coluna da variavel
		 * @param value		vetor de possibilidades
		 */
		private void setValues(int i, int j, boolean[] value) {
			i = i*d*d+j*d;
			for(int k=0; k<d; k++) {
				fc[i+k] = value[k];
			}
			
		}
		
		/**
		 * Atribui true ou false para todas as possibilidades de uma variavel.
		 * @param i			linha da variavel
		 * @param j			coluna da variavel
		 * @param b			true ou false
		 */
		private void setValues(int i, int j, boolean b) {
			i = i*d*d+j*d;
			j = i+d;
			for(int k=i; k<j; k++)
				fc[k] = b;
		}
		
		/**
		 * Atribui true ou false para as possibilidades de um intervalo de valores de uma variavel.
		 * @param i			linha da variavel
		 * @param j			coluna da variavel
		 * @param b			true ou false
		 * @param from		inicio, incluso
		 * @param to		fim, nao incluso
		 */
		private void setValues(int i, int j, boolean b, int from, int to) {
			i = i*d*d+j*d;
			from += i;
			to += i;
			for(int k=from; k<to; k++)
				fc[k] = b;
		}
		
		/**
		 * Conta o numero de valores possiveis de uma variavel.
		 * @param i			linha da variavel
		 * @param j			coluna da variavel
		 * @return			numero de valores possiveis
		 */
		public int countValues(int i, int j) {
			i = i*d*d+j*d;
			j = i+d;
			int n = 0;
			for(int k=i; k<j; k++)
				n += fc[k] ? 1 : 0;
			return n;
		}
		
		/**
		 * Atualiza as possibilidades de cada variavel de acordo com uma atribuicao.
		 * @param i			linha da variavel em que sera feita a atribuicao
		 * @param j			coluna da variavel em que sera feita a atribuicao
		 * @param value		valor
		 */
		public void update(int i, int j, int v) {
			
			//Valores iguais na linha e na coluna
			for(int k=0; k<d; k++) {
					setValue(k, j, v, false);
					setValue(i, k, v, false);
			}
			
			//Valores possiveis da variavel
			setValues(i, j, false);
			setValue(i, j, v, true);
			
			//Restricoes
			for(int k=1; k<board[i][j].length; k++) {
				switch(board[i][j][k]) {
					
					//Maior que o valor acima
					case 0:	setValues(i-1, j, false, v, d);
							break;
					//Menor que o valor acima
					case 1:	setValues(i-1, j, false, 0, v+1);
							break;
					//Maior que o valor a direita
					case 2:	setValues(i, j+1, false, v, d);
							break;
					//Menor que o valor a direira
					case 3:	setValues(i, j+1, false, 0, v+1);
							break;
					//Maior que o valor abaixo
					case 4:	setValues(i+1, j, false, v, d);
							break;
					//Menor que o valor abaixo
					case 5:	setValues(i+1, j, false, 0, v+1);
							break;
					//Maior que o valor a esquerda
					case 6: setValues(i, j-1, false, v, d);
							break;
					//Menor que o valor a esquerda
					case 7:	setValues(i, j-1, false, 0, v+1);
							break;
					}
				}
			
		}
		
		/**
		 * Constroi um ForwardChecking computando as possibilidades de cada variavel do futoshiki.
		 */
		public ForwardChecking() {
			fc = new boolean[d*d*d];
			
			for(int i=0; i<d; i++)
				for(int j=0; j<d; j++)
					setValues(i, j, values(i, j));
		}
		
		/**
		 * Construtor do proximo ForwardChecking para uma dada atribuicao.
		 * @param i				linha da atribuicao
		 * @param j				coluna da atribuicao
		 * @param v				valor
		 * @param prevFC		ForwardChecking anterior
		 */
		public ForwardChecking(int i, int j, int v, ForwardChecking prevFC) {
			fc = prevFC.getValues();
			this.update(i, j, v);
		}
		
	}
}