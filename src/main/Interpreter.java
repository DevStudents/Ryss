package main;
import java.util.*;
import javax.swing.JOptionPane;

import main.exceptions.InvalidVariableNameError;
import main.exceptions.UnknownExpressionException;
import main.functions.Add;
import main.functions.Init;
import main.functions.Log;
import main.variables.Variable;

/**
 * <h1>Interpreter</h1>
 * This is class where all magic take place.
 *
 * @author Szymon Kocur
 */

public class Interpreter {

	private String commentPattern;

	private Interpreter(String commentPattern) {
		this.commentPattern = commentPattern;
	}

	/**
	 * This is method which analyze .rx file for known expressions and then execute them.
	 *
	 * @param expressions It is list of expressions from the file.
	 * @throws UnknownExpressionException When scanner can't recognize expression.
	 * @see UnknownExpressionException
	 */
	public void scan(List<String> expressions) throws UnknownExpressionException {
		for(String expression : expressions) {
			if(!expression.startsWith(commentPattern) && !expression.isEmpty()) {
				String function = expression.substring(0, expression.indexOf(' '));
				String functionParams = expression.substring(expression.indexOf(' ') + 1);

				switch(function) {
					case "log":
						if(Variable.xVariables.get(functionParams) != null)
							Log.execute("" + ((Variable) Variable.xVariables.get(functionParams)).getValue());
						else
							Log.execute(functionParams);
						break;
					case "init":
						Init.initializeVariable(expression);
						break;
					case "add":
						try {
							String[] variables = expression.split(" ", 3);
							String var1 = ((Variable) Variable.xVariables.get(variables[1])).getName();
							if(Variable.xVariables.get(variables[2]) != null)
								Add.calculate(var1, ((Variable) Variable.xVariables.get(variables[2])).getName());
							else
								Add.calculate(var1, Integer.parseInt(variables[2]));
						} catch (InvalidVariableNameError error){
							System.out.println(error.toString());
						}
						break;
					case "MsgBox":
						JOptionPane.showMessageDialog(null, functionParams);
						break;
					default:
						throw new UnknownExpressionException();
				}
			}
		}
	}

	/**
	 * <h1>InterpreterBuilder</h1>
	 * Nested class based on Builder Pattern which allows adding more parameters without cluttering code.
	 *
	 * @author Szymon Kocur
	 * @see Interpreter
	 */
	public static class InterpreterBuilder {
		private String nestedCommentPattern;

		/**
		 * This method sets pattern for comments. Expression which starts with this pattern will be ignored.
		 *
		 * @param commentPattern
		 * @return InterpreterBuilder
		 */
		public InterpreterBuilder commentPattern(String commentPattern) {
			this.nestedCommentPattern = commentPattern;
			return this;
		}

		/**
		 * It returns object of Interpreter type.
		 *
		 * @return Interpreter
		 */
		public Interpreter build() {
			return new Interpreter(nestedCommentPattern);
		}
	}
}