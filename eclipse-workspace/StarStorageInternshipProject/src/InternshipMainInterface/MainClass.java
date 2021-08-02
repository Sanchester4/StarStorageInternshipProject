package InternshipMainInterface;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import InternshipDatabase.DatabaseInterface;
import InternshipDatabase.DbFunctions;
import InternshipUtils.RegexClass;
import InternshipDatabase.DbUtils;

public class MainClass {

	public static void main(String args[]) throws SQLException {
		DatabaseInterface demo = new DbFunctions();
		DbUtils jparser = new DbUtils();
		jparser.setUrlFile("C:\\Users\\Marck\\Desktop\\outpu1.json");
		if (jparser.getUrlFile() != null) {
			demo.createDb();
			jparser.getDataJson();
		}
		loopInput();
	}

	public static void loopInput() {
		DatabaseInterface demo = new DbFunctions();
		RegexClass regex = new RegexClass();
		String result = null;
		try {
			try (Scanner scanner = new Scanner(System.in)) {
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split("\\s");
					result = StringUtils.join(tokens, " ");
					Pattern p = Pattern.compile("EXPORT", Pattern.CASE_INSENSITIVE);
					Predicate<String> predicate = p.asPredicate();
					if (predicate.test(result) == true) {
						String urls = result.substring(7);
						DbUtils jparser1 = new DbUtils();
						jparser1.writeDataJson(urls);
						System.out.println("The export file was writted!");
						continue;
					}
					if (result.toLowerCase().equals("exit")) {
						demo.freeDb();
						System.out.println("You have exited from program!");
						System.exit(0);
					} else {
						regex.match(result);
					}
				}
				scanner.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
