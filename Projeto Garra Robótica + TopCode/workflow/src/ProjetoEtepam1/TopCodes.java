package ProjetoEtepam1;

//Danilo Martins Fialho, Novembro de 2015

/* Usando a WebCam e a bilbioteca TopCodes 
 * para aplicação de visão computacional em robótica
 * http://users.eecs.northwestern.edu/~mhorn/topcodes/
*/
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


public class TopCodes extends JFrame 
{

  private TCPainel topCodesPanel;


  public TopCodes()
  {
    super("Projeto ETEPAM TopCodes e Arduino");
    														//Iniciando o TC Painel (Janela de imagem TopCodes)
    Container c = getContentPane();
    topCodesPanel = new TCPainel();
    c.add( topCodesPanel);
    addWindowListener( new WindowAdapter() {
    														//Função para sair da Janela
    	public void windowClosing(WindowEvent e)
      { topCodesPanel.closeDown();    						
        System.exit(0);
      }
    });
    														//Propriedades do TCPainel
    setResizable(false);
    pack();

    														// Posição na Tela
    Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = scrDim.width - getWidth();
    int y = (scrDim.height - getHeight())/2;
    setLocation(x,y);

    setVisible(true); 										//Mostra a Janela do Projeto
  }


  // -------------------------------------------------------

  public static void main( String args[] )
  {  new TopCodes();  }

}
