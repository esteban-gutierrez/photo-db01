package es.te.ban;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

public class SaveImageApp {

    private static String PATH_TO_IMAGE = "resources/image01.jpg";

    public static void main(String[] args) {

        try {
            Mongo mongo = new Mongo("localhost", 27017);
            DB db = mongo.getDB("imagedb");
            DBCollection collection = db.getCollection("dummyColl");

            String newFileName = "mkyong-java-image";

            File imageFile = new File(getFileFromPath(PATH_TO_IMAGE));

            // create a "photo" namespace
            GridFS gfsPhoto = new GridFS(db, "photo");

            // get image file from local drive
            GridFSInputFile gfsFile = gfsPhoto.createFile(imageFile);

            // set a new filename for identify purpose
            gfsFile.setFilename(newFileName);

            // save the image file into mongoDB
            gfsFile.save();

            // print the result
            DBCursor cursor = gfsPhoto.getFileList();
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }

            // get image file by it's filename
            GridFSDBFile imageForOutput = gfsPhoto.findOne(newFileName);

            // save it into a new image file
            imageForOutput.writeTo(getFileFromPath(PATH_TO_IMAGE));

            // remove the image file from mongoDB
            gfsPhoto.remove(gfsPhoto.findOne(newFileName));

            System.out.println("Done");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileFromPath(String path) {
        ClassLoader classLoader = SaveImageApp.class.getClassLoader();
        return classLoader.getResource(path).getFile();
    }
}
