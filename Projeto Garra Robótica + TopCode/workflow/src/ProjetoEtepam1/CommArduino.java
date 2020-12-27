package ProjetoEtepam1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.swing.JOptionPane;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;

public class CommArduino {
	private static OutputStream serialOut;
	private static InputStream serialIn;
	  
  private int taxa;
  private String portaCOM;
  

  public CommArduino(String portaCOM, int taxa) {
    this.portaCOM = portaCOM;
    this.taxa = taxa;
    this.initialize();
  }     
 
  /**
   * M�doto que verifica se a comunica��o com a porta serial est� ok
   */
  private void initialize() {
    try {
      //Define uma vari�vel portId do tipo CommPortIdentifier para realizar a comunica��o serial
      CommPortIdentifier portId = null;
      try {
        //Tenta verificar se a porta COM informada existe
        portId = CommPortIdentifier.getPortIdentifier(this.portaCOM);
      }catch (NoSuchPortException npe) {
        //Caso a porta COM n�o exista ser� exibido um erro 
        JOptionPane.showMessageDialog(null, "Porta COM n�o encontrada.",
                  "Porta COM", JOptionPane.PLAIN_MESSAGE);
      }
      //Abre a porta COM 
      SerialPort port = (SerialPort) portId.open("Comunica��o serial", this.taxa);
      serialOut = port.getOutputStream();
      serialIn = port.getInputStream();
      port.setSerialPortParams(this.taxa, //taxa de transfer�ncia da porta serial 
                               SerialPort.DATABITS_8, //taxa de 10 bits 8 (envio)
                               SerialPort.STOPBITS_1, //taxa de 10 bits 1 (recebimento)
                               SerialPort.PARITY_NONE); //receber e enviar dados
    }catch (Exception e) {
      e.printStackTrace();
    }
}

  /**
   * M�todo que fecha a comunica��o com a porta serial
   */
  public void close() {
    try {
        serialOut.close();
    }catch (IOException e) {
      JOptionPane.showMessageDialog(null, "N�o foi poss�vel fechar porta COM.",
                "Fechar porta COM", JOptionPane.PLAIN_MESSAGE);
    }
  }

  /**
   * @param opcao - Valor a ser enviado pela porta serial
   */
  public void enviaDados(int opcao){
    try {
      serialOut.write(opcao);//escreve o valor na porta serial para ser enviado
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(null, "N�o foi poss�vel enviar o dado. ",
                "Enviar dados", JOptionPane.PLAIN_MESSAGE);
    }
  }
  public void receberDados(byte[] opcaoin){
	  try {
		  serialIn.read(opcaoin);
		  System.out.println("Valor Recebido: ->"+opcaoin);
	} catch (Exception e) {
		JOptionPane.showMessageDialog(null, "N�o foi poss�vel Receber o dado. ",
                "Receber dados", JOptionPane.PLAIN_MESSAGE);
	}
	  }
}
 
	