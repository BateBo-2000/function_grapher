package com.example.demo2;
//Bobu
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;


public class HelloController implements Initializable {
    double high=10,low=(-1)*high,step=1,zoom=high;
    double zoomer=high/100;
    int scale=1;
    double preciseIndex=.1; // po malko po precizno i po bavno
    Stack<Double> pointsStackX = new Stack();
    Stack<Double> pointsStackY = new Stack();
    boolean drawn=false;

    @FXML
    private AnchorPane pane,wp;

    @FXML
    private TextField functionTxt;

    @FXML
    private ImageView iamge;

    @FXML
    void Clearbtn(ActionEvent event) {
        functionTxt.setText("");
        high=10;
        low=-10;
        step=10;
        drawn=false;
        wp.getChildren().clear();
        Axis();
    }

    @FXML
    void Exitbtn(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private Button cbtn,sbtn,ebtn;

    String function="";
    @FXML
    void Showbtn(ActionEvent event) {
        System.out.println("button clicked");
       function = functionTxt.getText();
       drawf();
       drawn=true;

    }

    @FXML
    void zoom(ScrollEvent event) {
        /*
        izpolzva scroll za da zoomva in/out
        4rez promqna na stojnbostite napisani vurhu coordinatnata systema
        */
        if (event.getDeltaY()>0){
            //zoom in
            // capping the zoom in because NumberAxis dont allow step smaller than 0.1 or im dumb
            if(high>0.1){
                     /*
                     kogato zoomvame ot high( kraq na vidimata liniq) vadim step(tova prez kolko e razgelena liniqta)
                     za da si suotvetsva goleminata na 4islata durjim step da e = na mejdu 1 i 10 puti high.
                     IDEA FROM BOYAN SINILKOV
                     */
                if(high==step){
                    step/=10;
                    scale/=10;
                }
                high-=step;
                low+=step;
                zoomer=high/100;

                        /*
                         triem starite linii i risuvame novi kato izpolzvame metoda
                         realno sega kato gledam low high i step moje6e da ne sa globalni ama taka e po udobno i ne trqq da gi vuvejdam parametri vseki put 6tom polzvam metoda
                         */
                wp.getChildren().clear();       //delete old functions
                if(drawn)
                drawf();
                //new function
                zoom=high;
                Axis();
            }
        }else{
            //capping the max zoomout value to 1 mil
            if(high<1000000) {
                //zoom out
            /*
             gore sum opisal kakvo pravi zoom-a
             zoom outa e obratnoto
             */
                if (high == 10 * step) {
                    step *= 10;
                    scale *= 10;
                }
                high += step;
                low -= step;
                zoomer = high / 100;

                wp.getChildren().clear();       //delete old functions
                if (drawn)
                    drawf();
//            System.out.println("zoom = "+zoom);
//            System.out.println("high = "+high);
                //new function

                zoom = high;
                Axis();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wp.setPadding(new Insets(15));      //suzdava padding (nz dali raboti)(ne si li4i mnogo)
        Axis();
    }

    public void Axis() {
        //530  635- golemina na panel za 4ertaene
        /*
        chertaq liniite s globalnite promenlivi koito sum opredelil po gore
         */

        NumberAxis y = new NumberAxis(low,high,step);
        wp.getChildren().add(0,y);
        y.setLayoutX(317.5);
        y.setLayoutY(15);
        y.prefWidth(10);
        y.setSide(Side.LEFT);
        y.setMinorTickVisible(true);
        y.setPrefSize(0,500);

        NumberAxis x = new NumberAxis(low,high,step);
        wp.getChildren().add(1,x);
        x.setLayoutX(15);
        x.setLayoutY(265);
        x.prefWidth(10);
        x.setSide(Side.BOTTOM);
        x.setMinorTickVisible(true);
        x.setPrefSize(605,0);

    }

    // (0;0) at
    private static final double midx=635/2;
    private static final double midy=530/2;


    public void drawf(){
        try {

            for (double i = 100*zoomer; i > -100*zoomer; i -= preciseIndex*zoomer) {
                pointsStackX.push((((i*3.025)*100)/high)+     midx);
                pointsStackY.push( (((Function(i,function)*2.5) * 100)/high)  +    midy);
                //System.out.println(" pointstackX "+i+" "+((((i*3.025)*10)/high)+     midx));
            }
            //Ivu
            for (double i = 100*zoomer; i > -100*zoomer; i -= preciseIndex*zoomer) {
                if(pointsStackX.peek()>14 && pointsStackX.peek()<621 && pointsStackY.peek()>14 && pointsStackY.peek()<516){
                    Line line = new Line();
                    line.setStartX(pointsStackX.peek());
                    line.setStartY(pointsStackY.peek());
                    //System.out.println("point stack x "+ pointsStackX.peek());
                    pointsStackX.pop();
                    pointsStackY.pop();
                    if(pointsStackX.isEmpty()==false&&pointsStackY.isEmpty()==false && pointsStackX.peek()>15 && pointsStackX.peek()<620 && pointsStackY.peek()>15 && pointsStackY.peek()<515){
                        line.setEndX(pointsStackX.peek());
                        line.setEndY(pointsStackY.peek());
                    }
                    if(line.getEndX()!=0 && line.getEndY()!=0){
                        wp.getChildren().add(line);
                    }
                }else{
                    pointsStackX.pop();
                    pointsStackY.pop();
                }
            }
        }catch (EmptyStackException error){
            System.out.println("EmptyStackException errr");
        }

    }
    int a=0;    //testing purposes
    // MI6U
    public double Function(double x,String function){
//        System.out.println(" function = "+a);
//        a++;
        double result;
        int num = 0;
        try {
            //Премахваме всички интервали от нашия низ.
            function = function.replace(" ", "");
            //Методът split() разделя нашия String в списък. Този списък съдържа всички операции, които се извършват в нашата функция.
            ArrayList<String> functionOp = new ArrayList<>(Arrays.asList(function.split("[-9.0-9x^]")));
            //Премахваме празните елементи от нашия лист.
            functionOp.removeAll(Collections.singleton(""));
            //Методът split() разделя нашия String в масив. Този масив съдържа всички едночлени на нашата функция.
            String[] inputArr = function.split("[*/+]");
            //Цикъл, който представя степенуването в нашата функция като произведение.
            for (int i = 0; i < inputArr.length; i++) {
                if (inputArr[i].contains("x^")) {
                    num = Integer.parseInt(inputArr[i].substring(inputArr[i].indexOf('^') + 1));
                    for (int j = 1; j <= num; j++) {
                        if (j == 1) {
                            inputArr[i] = "x";
                        } else {
                            inputArr[i] += "*x";
                        }
                    }
                }
            }
            //Конкатенираме нашата функция, която беше разделена в два масива, в стринг.
            function = inputArr[0];
            for (int i = 1; i < inputArr.length; i++) {
                function += functionOp.get(i - 1) + inputArr[i];
            }
            //Заместваме х от нашата функция с подадената за аргумент стойност.
            function = function.replace("x", Double.toString(x));
            //Изолираме всяка операция в масив.
            String[] numbers1 = function.split("[+]");
            //Чрез цикъл, изчисляваме нашата функция.
            for (int i = 0; i < numbers1.length; i++) {
                //Проверяваме дали във всеки едночлен има умножение или деление.
                if (numbers1[i].contains("*") || numbers1[i].contains("/")) {
                    //Разделяме едночленът, в две структури от данни като едната структура, съдържа числата на едночлена, а другата съдържа дейсвията с тях: умножение или деление.
                    ArrayList<String> operations2 = new ArrayList<String>(Arrays.asList(numbers1[i].split("[-9.0-9.0]")));
                    operations2.removeAll(Collections.singleton(""));
                    //Добавяме празен първи елемент в нашия лист, за да се улесни изпълнението на цикъла.
                    operations2.add(0, "");
                    //Масив с операциите в едночлена:
                    String[] numbers2 = numbers1[i].split("[*/]");
                    //Промеливата запазва резлутата от изчислението на конкретния едночлен. Първоначално, тя запазва първия елемент от масива(който съдържа участниците в едночленна) и така цикълът няма нужда се се усложнява.
                    double tempResult = Double.parseDouble(numbers2[0]);
                    //Чрез цикъл, извършваме действията в едночлена. Цикълът минава и през двете структури и така изчислява конкретния едночлен.
                    for (int j = 1; j < operations2.size(); j++) {
                        if (operations2.get(j).equals("*")) {
                            tempResult *= Double.parseDouble(numbers2[j]);
                        } else if (operations2.get(j).equals("/")) {
                            tempResult /= Double.parseDouble(numbers2[j]);
                        }
                    }
                    //Замесваме изчисления едночлен в главния масив (numbers1) с едночлени.
                    numbers1[i] = Double.toString(tempResult);
                }
            }
            //Събираме всички едночлени.
            result = Double.parseDouble(numbers1[0]);
            for (int i = 1; i < numbers1.length; i++) {
                result += Double.parseDouble(numbers1[i]) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result*(-1);
//        return -1*Math.pow(x,2);
    }

}


