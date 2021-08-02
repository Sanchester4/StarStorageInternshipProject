package InternshipUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import InternshipDatabase.DbFunctions;

public class RegexClass extends DbFunctions {
	public RegexClass() {
	}

	public static Map<String, String> compareTemplate(String input, String template) {
		Matcher m = Pattern.compile("\\$(\\w*?)\\$", Pattern.CASE_INSENSITIVE).matcher(template);
		Map<String, String> vars = new LinkedHashMap<String, String>();
		while (m.find()) {
			vars.put(m.group(1), null);
		}

		String pattern = template;
		pattern = pattern.replaceAll("([?.])", "\\\\$1");
		for (String var : vars.keySet()) {
			pattern = pattern.replaceAll("\\$" + var + "\\$", "([\\\\w\\\\s]+?)");
		}

		m = Pattern.compile(pattern).matcher(input);
		if (m.matches()) {
			int i = 0;
			for (String var : vars.keySet()) {
				vars.put(var, m.group(++i));
			}
		} else {
			vars = null;
		}

		return vars;
	}

	public boolean match(String input) {
		input = input.toUpperCase();
		Map<String, String> vars = new LinkedHashMap<String, String>();
		vars = compareTemplate(input, "PRINT PRODUCTS CATEGORY $varcategory$");
		if (vars != null) {
			for (Map.Entry<String, String> entry : vars.entrySet()) {
				printProdCategory(entry.getValue());
			}
			return true;
		}

		Pattern pattern = Pattern.compile("PRINT PRODUCTS ALL");
		Predicate<String> predicate = pattern.asPredicate();
		predicate = pattern.asPredicate();
		if (predicate.test(input) == true) {
			printAllProd();
			return true;
		}

		vars = RegexClass.compareTemplate(input, "PRINT PRODUCTS $varname$");
		if (vars != null) {
			for (Map.Entry<String, String> entry : vars.entrySet()) {
				printProdName(entry.getValue());
			}
			return true;
		}

		pattern = Pattern.compile("PRINT CATEGORIES");
		predicate = pattern.asPredicate();
		if (predicate.test(input) == true) {
			printCategory();
			return true;
		}

		vars = RegexClass.compareTemplate(input, "BUY $varname$ $varquantity$ FOR $varusername$");
		if (vars != null) {
			int i = 0;
			String[] varProd = { "0", "0", "0" };
			for (Map.Entry<String, String> entry : vars.entrySet()) {
				varProd[i] = entry.getValue();
				i++;
			}
			int num1 = Integer.parseInt(varProd[1]);
			buyProd(varProd[0], num1, varProd[2]);
			return true;
		}
		pattern = Pattern.compile("BUY");
		predicate = pattern.asPredicate();
		if (predicate.test(input) == true) {
			getTime();
			System.out.println("Quantity must be numeric, integer and positive!");
			return false;
		}

		vars = RegexClass.compareTemplate(input, "REPLENISH $varname$ $varquantity$");
		if (vars != null) {
			String[] varProd = { "0", "0" };
			int i = 0;
			for (Map.Entry<String, String> entry : vars.entrySet()) {
				varProd[i] = entry.getValue();
				i++;
			}
			int num2 = Integer.parseInt(varProd[1]);
			replenishProdQuant(varProd[0], num2);
			return true;
		}

		vars = RegexClass.compareTemplate(input, "ADD NEW CATEGORY $varcategory$");
		if (vars != null) {
			for (Map.Entry<String, String> entry : vars.entrySet()) {
				addCategory(entry.getValue());
				printCategory();
				return true;
			}
		}

		vars = RegexClass.compareTemplate(input, "ADD NEW PRODUCT $varname$ $varcategory$ $varquantity$ $varprice$");
		if (vars != null) {
			int i = 0;
			String[] varProd = { "0", "0", "0", "0" };
			for (Map.Entry<String, String> entry : vars.entrySet()) {
				varProd[i] = entry.getValue();
				i++;
			}
			int num1 = Integer.parseInt(varProd[2]);
			int num2 = Integer.parseInt(varProd[3]);
			addNewProd(varProd[0], varProd[1], num1, num2);
			return true;
		}
		pattern = Pattern.compile("ADD NEW PRODUCT");
		predicate = pattern.asPredicate();
		if (predicate.test(input) == true) {
			getTime();
			System.out.println("Quantity and price must be numeric, integer and positive!");
			return false;
		}

		vars = RegexClass.compareTemplate(input, "REMOVE PRODUCT $varname$");
		if (vars != null) {
			for (Map.Entry<String, String> entry : vars.entrySet()) {
				removeProd(entry.getValue());
			}
			return true;
		}

		Pattern p = Pattern.compile("PRINT DISPLAY_MODE");
		predicate = p.asPredicate();
		if (predicate.test(input) == true) {
			if (fileOut == null) {
				System.out.println("Console");
				return true;
			} else {
				getTime();
				System.out.println(urlThis);
				return true;
			}
		}

		p = Pattern.compile("SWITCH DISPLAY_MODE FILE");
		predicate = p.asPredicate();
		if (predicate.test(input) == true) {
			pattern = Pattern.compile("\"(.*?)\"");
			Matcher m = pattern.matcher(input);
			String urls = null;
			if (m.find()) {
				urls = m.group(1);
			}
			readInput(urls);
			return true;
		}
		getTime();
		System.out.println("There's no such command!");
		return false;

	}

}
