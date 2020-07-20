/** Helper cleaing up main code
 * @author Jacob Vetere
 */
package memoryGame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.io.File;//For creating dir folder object
import java.io.IOException;//To catch issues with the file system (Required
import java.util.ArrayList;//For Storing strings of URI for images
import java.util.Objects;

public class util {
    //Iterates through a given directory finding files with .jpg at the end
    public static ArrayList<String> getImages (File dir) throws IOException {

        ArrayList<String> images = new ArrayList<>();

        File[] folder= dir.listFiles();

        for (File file: Objects.requireNonNull(folder)){
            if(file != null && file.getName().toLowerCase().endsWith(".jpg") ){
                images.add(file.getCanonicalPath());
                images.add(file.getCanonicalPath());
            }
        }
        return images;
    }
    public static VBox createRadio(){

        ToggleGroup tg =new ToggleGroup();

        RadioButton r1 = new RadioButton("Numbers");
        RadioButton r2 = new RadioButton("Letters");
        RadioButton r3 = new RadioButton("memoryGame/Images");

        r1.setToggleGroup(tg);
        r2.setToggleGroup(tg);
        r3.setToggleGroup(tg);

        VBox holder = new VBox(r1, r2, r3);

        holder.setMaxWidth(200);
        holder.setMaxHeight(200);

        r1.setAlignment(Pos.CENTER);
        r2.setAlignment(Pos.CENTER);
        r3.setAlignment(Pos.CENTER);

        holder.setPadding(new Insets(10,10,10,10));
        holder.setSpacing(10);

        r1.selectedProperty().addListener((obs, wasPreviouslySelected, isNowSelected) -> {

            if (isNowSelected) {

                Main.setType(1);

            }
        });

        r2.selectedProperty().addListener((obs, wasPreviouslySelected, isNowSelected) -> {

            if (isNowSelected) {

                Main.setType(2);

            }
        });

        r3.selectedProperty().addListener((obs, wasPreviouslySelected, isNowSelected) -> {

            if (isNowSelected) {

                Main.setType(3);

            }
        });

        return holder;
    }
    public static Rectangle makeNewRec(Paint color){
        Rectangle rtr_Rec = new Rectangle();
        rtr_Rec.setWidth(200);
        rtr_Rec.setHeight(200);
        rtr_Rec.setFill(color);
        return rtr_Rec;
    }
}
