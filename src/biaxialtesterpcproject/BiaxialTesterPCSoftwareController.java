/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package biaxialtesterpcproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.fazecast.jSerialComm.*;
import java.io.File;
import java.io.FileNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * FXML Controller class
 *
 * @author angel
 */
public class BiaxialTesterPCSoftwareController implements Initializable {

    @FXML
    private Button enable1;
    @FXML
    private Button enable2;
    @FXML
    private Button enable3;
    @FXML
    private Button enable4;
    @FXML
    private ComboBox<SerialPort> commPortSelector;
    @FXML
    private Button moveThreeExtX;
    @FXML
    private Button moveTwoExtX;
    @FXML
    private Button moveOneExtX;
    @FXML
    private Button homeX;
    @FXML
    private Button moveThreeExtY;
    @FXML
    private Button moveTwoExtY;
    @FXML
    private Button moveOneExtY;
    @FXML
    private Button homeY;
    @FXML
    private Button moveThreeExt;
    @FXML
    private Button moveTwoExt;
    @FXML
    private Button moveOneExt;
    @FXML
    private Button home;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private TextField sensor1Val;
    @FXML
    private TextField sensor2Val;
    @FXML
    private TextField sensor3Val;
    @FXML
    private TextField sensor4Val;
    @FXML
    private TextArea commandOut;
    @FXML
    private TextField commandInput;
    @FXML
    private Button fileChooser;
    @FXML
    private TextField fileName;
    @FXML
    private Button runProgram;

    public static SerialPort commPort;
    private boolean enabled1 = true, enabled2 = true, enabled3 = true, enabled4 = true;
    private boolean dirExt1 = true, dirExt2 = true, dirExt3 = true, dirExt4 = true;
    @FXML
    private Button updateComm;
    
    private File file;
    
    final FileChooser fc = new FileChooser();
    
    private boolean programProcess = false;
    
    private int milimetersX = 0;
    private int milimetersY = 0;
    private float[] forces;
    
    XYChart.Series<Number, Number> dataX = new XYChart.Series<>();
    XYChart.Series<Number, Number> dataY = new XYChart.Series<>();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SerialPort[] commports = SerialPort.getCommPorts();
        for(SerialPort eachCommPort: commports)
            System.out.println("Lista de puertos: " + eachCommPort.getDescriptivePortName());
        ObservableList<SerialPort> commPortsList = FXCollections.observableArrayList(commports);
        commPortSelector.getItems().addAll(commPortsList);
        
    } 
    
    //Cambiar el color de los botones de estado de los motores
    private void enableColor(int button){
        switch(button){
            case 1:
                if(enabled1){
                    enable1.setStyle("-fx-background-color: red; ");
                    enable1.setText("Enable");
                    enabled1 = false; 
                }
                else{
                    enable1.setStyle("-fx-background-color: green; ");
                    enable1.setText("Disable");
                    enabled1 = true;
                }
                break;
            
            case 2:
                if(enabled2){
                    enable2.setStyle("-fx-background-color: red; ");
                    enable2.setText("Enable");
                    enabled2 = false; 
                }
                else{
                    enable2.setStyle("-fx-background-color: green; ");
                    enable2.setText("Disable");
                    enabled2 = true;
                }
                break;
                
            case 3:
                if(enabled3){
                    enable3.setStyle("-fx-background-color: red; ");
                    enable3.setText("Enable");
                    enabled3 = false;
                }
                else{
                    enable3.setStyle("-fx-background-color: green; ");
                    enable3.setText("Disable");
                    enabled3 = true;
                }
                break;
                
            case 4:
                if(enabled4){
                    enable4.setStyle("-fx-background-color: red; ");
                    enable4.setText("Enable");
                    enabled4 = false;
                }
                else{
                    enable4.setStyle("-fx-background-color: green; ");
                    enable4.setText("Disable");
                    enabled4 = true;
                }
                break;
        }
    }
    
    //Activación/Desactivación de los motores
    @FXML
    private void enable1(ActionEvent event) throws InterruptedException {
        String data = "e0";
        byte[] b = data.getBytes();
        if(!programProcess){
            enableColor(1);
            if(commPort != null){
                commPort.writeBytes(b, data.getBytes().length);
                Thread.sleep(2000);
            }
        }
    }

    @FXML
    private void enable2(ActionEvent event) throws InterruptedException {
        String data = "e1";
        byte[] b = data.getBytes();
        if(!programProcess){
            enableColor(2);
            if(commPort != null){
                commPort.writeBytes(b, data.getBytes().length);
                Thread.sleep(2000);
            }
        }
    }

    @FXML
    private void enable3(ActionEvent event) throws InterruptedException {
        String data = "e2";
        byte[] b = data.getBytes();
        if(!programProcess){
            enableColor(3);
            if(commPort != null){
                commPort.writeBytes(b, data.getBytes().length);
                Thread.sleep(2000);
            }
        }
    }

    @FXML
    private void enable4(ActionEvent event) throws InterruptedException {
        String data = "e3";
        byte[] b = data.getBytes();
        if(!programProcess){
            enableColor(4);
            if(commPort != null){
                commPort.writeBytes(b, data.getBytes().length);
                Thread.sleep(2000);
            }
        }
    }
    
    //Selector de archivo
    @FXML
    private void chooseFile(ActionEvent event) {
        if(!programProcess){
            fc.setTitle("My file chooser");
            file = fc.showOpenDialog(null);
            fileName.appendText(file.getAbsolutePath());
        }
    }

    //Empezar a ejecutar el archivo seleccionado
    @FXML
    private synchronized void runProgram(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            var xAxis = new NumberAxis();
            xAxis.setLabel("Milimeters");

            var yAxis = new NumberAxis();
            yAxis.setLabel("Newtons");

            graph = new LineChart<>(xAxis, yAxis);
            graph.setTitle("Strain test");
            
            dataX.getData().clear();
            dataY.getData().clear();
            File doc = file;  
            MyThread mt = new MyThread();
            mt.doc = doc;
            Thread t = new Thread(mt);
            t.start(); 
            programProcess = true;
        }
    }

    //Actualizar el puerto de comunicación
    @FXML
    private void updatePressed(ActionEvent event) throws InterruptedException {
        commPort = commPortSelector.getValue();
        commPort.setComPortParameters(115200,8,SerialPort.ONE_STOP_BIT,SerialPort.NO_PARITY);
        commPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 3000, 0);
        commPort.openPort();
        //Esperar si el Arduino se está reiniciando
        Thread.sleep(2000);
        
        //Listener para la recepción de información del puerto serie
        commPort.addDataListener(new SerialPortDataListener() {
            private String msg = "";
        @Override
        public int getListeningEvents() {            
          return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }
        @Override
        public void serialEvent(SerialPortEvent event) {
          String correctMessage = "";
          if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED){
                
                //byte [] data = event.getReceivedData();
                //String msg = new String(data);
                msg += new String(event.getReceivedData());
                if(msg.charAt(0) != 's'){
                while (msg.contains("\n")) {
			String[] message = msg.split("\\n", 2);
			msg = (message.length > 1) ? message[1] : "";
                        correctMessage = message[0];
			System.out.println("Message: " + message[0]);
		}
                //System.out.println(msg);
                String[] measures = correctMessage.split("\t");
                
                milimetersX = Integer.parseInt(measures[0]);
                milimetersY = Integer.parseInt(measures[1]);
                
                for(int i = 0; i < 4; i++){
                    forces[i] = Integer.parseInt(measures[i+2]);
                }
                graph.getData().clear();
                
                dataX.setName("Force X Axis");
                dataX.getData().add(new XYChart.Data<>(milimetersX, (forces[0] + forces[1])/2));
                
                dataY.setName("Force Y Axis");
                dataY.getData().add(new XYChart.Data<>(milimetersY, (forces[2] + forces[3])/2));
                
                graph.getData().add(dataX);
                graph.getData().add(dataY);
                //Actualización valor fuerza sensores
                try{
                sensor1Val.setText(measures[2]);
                sensor2Val.setText(measures[3]);
                sensor3Val.setText(measures[4]);
                sensor4Val.setText(measures[5]);
                }
                catch(Exception e){} 
            }
                else{
                    while (msg.contains("\n")) {
			String[] message = msg.substring(1).split("\\n", 2);
			msg = (message.length > 1) ? message[1] : "";
                        correctMessage = message[0];
			System.out.println("Message: " + message[0]);
                    }
                    String[] directions = correctMessage.split(" ");
                    dirExt1 = Integer.parseInt(directions[0]) == 1;
                    dirExt2 = Integer.parseInt(directions[1]) == 1;
                    dirExt3 = Integer.parseInt(directions[2]) == 1;
                    dirExt4 = Integer.parseInt(directions[3]) == 1;
                    //System.out.println(String.valueOf(dirExt1));
                }
          }
        }
      });
    }
    
    //Mandar comando por terminal
    @FXML
    private void onEnterPressedCommand(KeyEvent event) throws InterruptedException {
        String command = "";
        byte[] b;
        if(!programProcess){
            if (event.getCode().equals(KeyCode.ENTER)) {
                command = commandInput.getText();
                commandInput.setText("");
                commandOut.setText(commandOut.getText() + "\n" + command);
                commandOut.setScrollTop(Double.MAX_VALUE);
                if(command.startsWith("e")){
                    switch(command.charAt(1)){
                        case '0':
                            enableColor(1);
                            break;
                            
                        case '1':
                            enableColor(2);
                            break;
                            
                        case '2':
                            enableColor(3);
                            break;
                            
                        case '3':
                            enableColor(4);
                            break;
                    }
                }
                b = command.getBytes();
                if(commPort != null)
                    commPort.writeBytes(b, command.getBytes().length);
                Thread.sleep(2000);
            }
        }
    }
    
    //Movimiento de ejes por botones de interfaz
    @FXML
    private void moveThreeExtX(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnExtDirection(1);
            turnExtDirection(2);
            String data = "m450";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveTwoExtX(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnExtDirection(1);
            turnExtDirection(2);
            String data = "m410";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveOneExtX(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnExtDirection(1);
            turnExtDirection(2);
            String data = "m41";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveOneIntX(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnIntDirection(1);
            turnIntDirection(2);
            turnIntDirection(3);
            turnIntDirection(4);
            String data = "m41";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveTwoIntX(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnIntDirection(1);
            turnIntDirection(2);
            turnIntDirection(3);
            turnIntDirection(4);
            String data = "m410";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveThreeIntX(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnIntDirection(1);
            turnIntDirection(2);
            turnIntDirection(3);
            turnIntDirection(4);
            String data = "m450";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveThreeExtY(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnExtDirection(3);
            turnExtDirection(4);
            String data = "m550";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveTwoExtY(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnExtDirection(3);
            turnExtDirection(4);
            String data = "m510";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveOneExtY(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnExtDirection(3);
            turnExtDirection(4);
            String data = "m51";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveOneIntY(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnIntDirection(1);
            turnIntDirection(2);
            turnIntDirection(3);
            turnIntDirection(4);
            String data = "m51";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveTwoIntY(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnIntDirection(1);
            turnIntDirection(2);
            turnIntDirection(3);
            turnIntDirection(4);
            String data = "m510";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveThreeIntY(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnIntDirection(1);
            turnIntDirection(2);
            turnIntDirection(3);
            turnIntDirection(4);
            String data = "m550";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveThreeExt(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnExtDirection(1);
            turnExtDirection(2);
            turnExtDirection(3);
            turnExtDirection(4);
            String data = "m650";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveTwoExt(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnExtDirection(1);
            turnExtDirection(2);
            turnExtDirection(3);
            turnExtDirection(4);
            String data = "m610";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveOneExt(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnExtDirection(1);
            turnExtDirection(2);
            turnExtDirection(3);
            turnExtDirection(4);
            String data = "m61";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveOneInt(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnIntDirection(1);
            turnIntDirection(2);
            turnIntDirection(3);
            turnIntDirection(4);
            String data = "m61";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveTwoInt(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnIntDirection(1);
            turnIntDirection(2);
            turnIntDirection(3);
            turnIntDirection(4);
            String data = "m610";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }

    @FXML
    private void moveThreeInt(ActionEvent event) throws InterruptedException {
        if(!programProcess){
            turnIntDirection(1);
            turnIntDirection(2);
            turnIntDirection(3);
            turnIntDirection(4);
            String data = "m650";
            byte[] b = data.getBytes();
            if(commPort != null)
                commPort.writeBytes(b, data.getBytes().length);
            Thread.sleep(2000);
        }
    }
    
    //Cambiar dirección de motores dependiendo de su uso
    private void turnExtDirection(int motors) throws InterruptedException{
        switch(motors){
            case 1:
                if(dirExt1 == false) {
                    String data = "d0";
                    dirExt1 = true;
                    byte[] b = data.getBytes();
                    if(commPort != null)
                        commPort.writeBytes(b, data.getBytes().length);
                    Thread.sleep(2000);
                }
                break;
            case 2:
                if(dirExt2 == false) {
                    String data = "d1";
                    dirExt2 = true;
                    byte[] b = data.getBytes();
                    if(commPort != null)
                        commPort.writeBytes(b, data.getBytes().length);
                    Thread.sleep(2000);
                }
                break;
            case 3:
                if(dirExt3 == false) {
                    String data = "d2";
                    dirExt3 = true;
                    byte[] b = data.getBytes();
                    if(commPort != null)
                        commPort.writeBytes(b, data.getBytes().length);
                    Thread.sleep(2000);
                }
                break;
            case 4:
                if(dirExt4 == false) {
                    String data = "d3";
                    dirExt4 = true;
                    byte[] b = data.getBytes();
                    if(commPort != null)
                        commPort.writeBytes(b, data.getBytes().length);
                    Thread.sleep(2000);
                }
                break;
            default:
                break;
        }
    }
    
    private void turnIntDirection(int motors) throws InterruptedException{
        switch(motors){
            case 1:
                if(dirExt1 == true) {
                    String data = "d0";
                    dirExt1 = false;
                    byte[] b = data.getBytes();
                    if(commPort != null)
                        commPort.writeBytes(b, data.getBytes().length);
                    Thread.sleep(2000);
                }
                break;
            case 2:
                if(dirExt2 == true) {
                    String data = "d1";
                    dirExt2 = false;
                    byte[] b = data.getBytes();
                    if(commPort != null)
                        commPort.writeBytes(b, data.getBytes().length);
                    Thread.sleep(2000);
                }
                break;
            case 3:
                if(dirExt3 == true) {
                    String data = "d2";
                    dirExt3 = false;
                    byte[] b = data.getBytes();
                    if(commPort != null)
                        commPort.writeBytes(b, data.getBytes().length);
                    Thread.sleep(2000);
                }
                break;
            case 4:
                if(dirExt4 == true) {
                    String data = "d3";
                    dirExt4 = false;
                    byte[] b = data.getBytes();
                    if(commPort != null)
                        commPort.writeBytes(b, data.getBytes().length);
                    Thread.sleep(2000);
                }
                break;
            default:
                break;
        }
    }
    
    //Hilo que gestiona la lectura de los datos 
    class MyThread implements Runnable
    {
        public File doc = null;
        Scanner scan = null;
        byte[] b;
        public synchronized void run()
        {
            try {
            scan = new Scanner(doc);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BiaxialTesterPCSoftwareController.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (scan.hasNextLine()){
            if(commPort != null)
                commPort.writeBytes(b, scan.nextLine().getBytes().length);
                try {
                    Thread.sleep(2000);
                    commPort.writeBytes(b, scan.nextLine().getBytes().length);
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BiaxialTesterPCSoftwareController.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        programProcess = false;
        }
    }
}
