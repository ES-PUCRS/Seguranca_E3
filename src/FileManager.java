import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.Scanner;
import java.io.File;

/**
 * Disclaimer *
 * On this development, LinkedHashMap was chosen due to be a map, being able to
 * store key and value which is planned within the file.
 * Thus, a LinkedHashMap was chosen besides a HashMap since it keeps the entry
 * order, avoiding change spaces places and keeping file organized as the sense
 * wanted.
 */
public class FileManager {

    /**
     * A full path to the output file, containing the folder path, file name and
     * file extension
     */
    public final String file;

    /**
     * A singleton instance to avoid many instances of this class
     */
    private static FileManager instance;

    /**
     * Since it is used on multiple computers, the project are using CanonicalPath
     * to self localizate to be able to generate the output file with the
     * information got from the runtime
     * 
     * @throws IOException
     *                     If an I/O error occurs, which is possible because the
     *                     construction of the canonical pathname may require
     *                     filesystem queries
     * 
     * @see Path#toRealPath
     */
    private FileManager() throws IOException {
        this.file = new File(".")
                .getCanonicalPath()
                .replace("/out", "/")
                + "keys.txt";
    }

    /**
     * Returns a singleton instance of this class.
     * Since it is a simple project without code complexity and well defined code
     * patterns and fully concerned with good code pratices, there wasn't planned to
     * add this class as Singleton interface due to avoid time disposal.
     * 
     * @return FileManager: An instance from this class
     * 
     * @see https://docs.oracle.com/javaee/7/api/javax/ejb/Singleton.html
     */
    private static FileManager instantiate() throws IOException {
        if (instance == null)
            instance = new FileManager();
        return instance;
    }

    /**
     * 
     * Especial handle * Some entries as space_[0-9]=null are ignored and wrote only
     * spaces on the file to avoid an aweful aspect.
     * 
     * @param LinkedHashMap<String,String> All entries from this param will be
     *                                     updated on the LinkedHashMap generated
     *                                     from the read and they will be wrote
     *                                     again in the file.
     * 
     * @see LinkedHashMap
     */
    public static void publish(LinkedHashMap<String, String> data) {
        try {
            LinkedHashMap<String, String> file = read();
            PrintStream fileStream = new PrintStream(instance.file);
            file.putAll(data);

            for (Entry<String, String> entry : file.entrySet()) {
                fileStream.println(
                        (entry.getKey().matches("space_\\d+")) ? "" : entry.getKey() + ": " + entry.getValue());
            }

            fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a string with the value found within file
     * 
     * @return string: value from file entry
     * 
     * @throws IOException from the read
     * 
     * @see read
     */
    public static String getFileValue(final String value) throws IOException {
        return read().get(value);
    }

    /**
     * Returns private LinkedHashMap<String, String> read from the file keys.txt
     * (the file name is set on constructor)
     * Where the Map key will be the name before the colon and the value will be
     * after colon.
     * For example:
     * keys.txt >>
     * * foo: bar
     * Map >>
     * * [foo=bar]
     * 
     * Especial handle * Some entries will be added as space_[0-9]=null to be able
     * to create multiple entry spaces without major effort.
     * Those spaces entries must be handled on the publish method to avoid write
     * those aweful comments inside the file.
     * 
     * @return LinkedHashMap<String,String>: with all entries read from the file
     *
     * @throws FileNotFoundException if source is not found
     * @throws IOException           from the instantiate
     * 
     * @see LinkedHashMap
     */
    private static LinkedHashMap<String, String> read() throws IOException {
        instantiate();
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        Scanner scanner = new Scanner(new File(instance.file));
        int spaceCount = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String key = line.replaceAll(":.*", "");
            String value = line.replaceAll(".*:", "");

            if (key.isEmpty()) {
                key = "space_" + spaceCount;
                spaceCount++;
            }
            if (!value.isEmpty() && value.charAt(0) == 32) {
                value = value.substring(1, value.length());
            }

            map.put(key, value);
        }

        scanner.close();
        return map;
    }

}