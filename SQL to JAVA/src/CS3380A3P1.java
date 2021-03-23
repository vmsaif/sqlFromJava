import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author 		Saif Mahmud
 * @id          7808507
 * @version     Dec. 2, 2020
 * @instructor	R.Guderian
 * @assignment	03
 */
public class CS3380A3P1 {
	
	private static ArrayList<String[]> activeTable;
	private static ArrayList<String[]> peopleTable;
	private static ArrayList<String[]> coursesTable;
	private static ArrayList<String[]> phoneTable;
	public static void main(String[] args) {
		try {
			File activities = new File("activities.txt");
			File people = new File("people.txt");
			File courses = new File("courses.txt");
			File phone = new File("phone.txt");
			
			activeTable = new ArrayList<String[]>();
			peopleTable = new ArrayList<String[]>();
			coursesTable = new ArrayList<String[]>();
			phoneTable = new ArrayList<String[]>();
			
			makeTable(activities, activeTable);
			makeTable(people, peopleTable);
			makeTable(courses, coursesTable);
			makeTable(phone, phoneTable);
			
			System.out.println();
			System.out.println("//-------------------------------------//");
			System.out.println();
			System.out.println("DOING PEOPLE LEFT JOIN ACTIVITIES:");
			System.out.println();
			System.out.println("This query is supposed to show \"Null\" from \n"
					+ "Activities table as there are some people \n"
					+ "who didn't do any activities. \n"
					+ "In this case: ID-7");
			System.out.println();
			printTable(leftJoin(peopleTable,activeTable));
			System.out.println();
			System.out.println("//-------------------------------------//");
			
			//------------------------------------------------//
			
			System.out.println();
			System.out.println("//-------------------------------------//");
			System.out.println();
			System.out.println("DOING SWAP - ACTIVITIES LEFT JOIN PEOPLE:");
			System.out.println();
			System.out.println("This query is supposed to show \"Null\" from \n"
					+ "PEOPLE table as there are some Activities \n"
					+ "which wasn't done any people. \n"
					+ "In this case: ID-1000");
			System.out.println();
			printTable(leftJoin(activeTable,peopleTable));
			System.out.println();
			System.out.println("//-------------------------------------//");
			
			//------------------------------------------------//
			
			System.out.println();
			System.out.println("//-------------------------------------//");
			System.out.println();
			System.out.println("DOING PEOPLE OUTER JOIN ACTIVITIES:");
			System.out.println();
			System.out.println("This query is supposed to show \"Null\" from \n"
					+ "BOTH tables as there are some Activities \n"
					+ "which wasn't done any people and vice-versa. \n"
					+ "In this case: ID-1000,7");
			System.out.println();
			printTable(outerJoin(activeTable,peopleTable));
			System.out.println();
			System.out.println("//-------------------------------------//");
			
			//------------------------------------------------//
			
			System.out.println();
			System.out.println("//-------------------------------------//");
			System.out.println();
			System.out.println("DOING PEOPLE NATURAL JOIN COURSES WITH NO MATCHING COLUMN:");
			System.out.println();
			System.out.println("This query is supposed to cross product "
					+ "BOTH tables as there are no matching columns between \n"
					+ "people and courses. So, 7X4 = 28 rows.");
			System.out.println();
			printTable(naturalJoin(peopleTable, coursesTable));
			System.out.println();
			System.out.println("//-------------------------------------//");
			
			//------------------------------------------------//
			
			System.out.println();
			System.out.println("//-------------------------------------//");
			System.out.println();
			System.out.println("DOING PEOPLE NATURAL JOIN ACTIVITIES WITH 1 MATCHING COLUMN:");
			System.out.println();
			System.out.println("This query is supposed to merge both table with 1 matching \n"
					+ "column and throw away the rest");
			System.out.println();
			printTable(naturalJoin(peopleTable, activeTable));
			System.out.println();
			System.out.println("//-------------------------------------//");

//------------------------------------------------//
			
			System.out.println();
			System.out.println("//-------------------------------------//");
			System.out.println();
			System.out.println("DOING PEOPLE NATURAL JOIN PHONE WITH > 1 MATCHING COLUMN:");
			System.out.println();
			System.out.println(" If both columns of PEOPLE matches with PHONE, then the row will show \n"
					+ " here we have the name \"Andy\" on both tables. However their id's won't match.\n"
					+ " Andy with id 6 will appear as normal. However, Andy with ID 9 won't appear here.\n"
					+ " which is in PHONE table but not in PEOPLE table");
			System.out.println();
			printTable(naturalJoin(peopleTable, phoneTable));
			System.out.println();
			System.out.println("//-------------------------------------//");
//			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//main
	public static ArrayList<String[]> naturalJoin(ArrayList<String[]> table1, ArrayList<String[]> table2) {
		ArrayList<String[]> output = new ArrayList<String[]>();
		int headerRow = 0;
		
		int[][] matchingColumns = new int[table1.get(0).length][table2.get(0).length+1];
		int indexRow = 0;
		int indexCol = 0;
		boolean found = false;
		
		//initialize the array with -1
		for(int i = 0; i < matchingColumns.length; i++) {
			Arrays.fill(matchingColumns[i], -1);
		}
		
		//check for matching columns
		for(int col1 = 0; col1 < table1.get(headerRow).length; col1++) {
			found = false;
			for(int col2 = 0; col2 < table2.get(headerRow).length; col2++) {
				if(table1.get(headerRow)[col1].equals(table2.get(headerRow)[col2])) {
					
					if(!found) {
						matchingColumns[indexRow][indexCol] = col1;
						indexCol++;
					} 	
					matchingColumns[indexRow][indexCol] = col2;
					indexCol++;
					found = true;
				}
			}
			if(found) {
				indexRow++;
				indexCol = 0;
			}
		}
		
		//if no matching column
		if(countRow(matchingColumns) < 1) {
			//do cross product
			output = crossProduct(table1, table2);
		} else {
				//first add all with 1 matching column
				int myRow = 0;
				int table1KeyCol = matchingColumns[myRow][0];
				int table2KeyCol = matchingColumns[myRow][1];
				
				for(int row1 = 1; row1 < table1.size(); row1++) {
					
					for(int row2 = 1; row2 < table2.size(); row2++) {
						String a = table1.get(row1)[table1KeyCol];
						String b = table2.get(row2)[table2KeyCol];
						if(a.equals(b)) {
							output.add(arrayJoiner(table1.get(row1),table2.get(row2)));
						}
					}
				}

				//then delete by not matching 2nd pair of columns
				int matchingColumnsRowSize = countRow(matchingColumns);
				myRow++;
				
				if(myRow < matchingColumnsRowSize) {
					
					table1KeyCol = matchingColumns[myRow][0];
					table2KeyCol = matchingColumns[myRow][1];
					
					for(int row1 = 1; row1 < table1.size(); row1++) {
						found = false;
						for(int row2 = 1; row2 < table2.size() && !found; row2++) {
							String a = table1.get(row1)[table1KeyCol];
							String b = table2.get(row2)[table2KeyCol];
							if(a.equals(b)) {
								found = true;
							}
						}
						if(!found) {
							output.remove(row1);
						}
					}//for
					myRow++;
				}
		}
		return output;
	}
	private static void dump2DArray(int[][] x) {
		for(int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[i].length; j++) {
				System.out.print(i+","+j +" is "+x[i][j] + " ");
			}
			System.out.println();
		}
	}
	private static int countRow(int[][] x) {
		int out = 0;
		for(int i = 0; i < x.length; i++) {
			if(x[i][0] != -1) {
				out++;
			}
		}
		return out;
	}
	private static ArrayList<String[]> crossProduct(ArrayList<String[]> table1, ArrayList<String[]> table2){
		ArrayList<String[]> output = new ArrayList<String[]>();
		for(int row = 1; row < table1.size(); row++) {
			for(int col = 1; col < table2.size(); col++) {
				output.add(arrayJoiner(table1.get(row), table2.get(col)));
			}
		}
		return output;
	}
	public static ArrayList<String[]> outerJoin(ArrayList<String[]> table1, ArrayList<String[]> table2) 
	{
		ArrayList<String[]> output = new ArrayList<String[]>();
		int headerRow = 0;
		int table1KeyCol = -1;
		int table2KeyCol = -1;
		boolean found = false;
		
		for(int i = 0; i < table1.get(headerRow).length && table1KeyCol == -1 && table2KeyCol == -1; i++) {
			for(int j = 0; j < table2.get(headerRow).length && table1KeyCol == -1 && table2KeyCol == -1; j++) {
				if(table1.get(headerRow)[i].equals(table2.get(headerRow)[j])) {
					table1KeyCol = i;
					table2KeyCol = j;
				}
			}
		}
		
		//left join
		for(int row1 = 1; row1 < table1.size(); row1++) {
			found = false;
			for(int row2 = 1; row2 < table2.size(); row2++) {
				if(table1.get(row1)[table1KeyCol].equals(table2.get(row2)[table2KeyCol])) {
					output.add(arrayJoiner(table1.get(row1),table2.get(row2)));
					found = true;
				}
			}
			if(!found) {
				output.add(addNullsAfter(table1.get(row1), table2.get(0).length));
			}
		}
		
		//right join
		
		for(int row2 = 1; row2 < table2.size(); row2++) {
			found = false;
			for(int row1 = 1; row1 < table1.size(); row1++) {
				if(table2.get(row2)[table2KeyCol].equals(table1.get(row1)[table1KeyCol])) {
					found = true;
				}
			}
			if(!found) {
				output.add(addNullsBefore(table2.get(row2), table1.get(0).length));
			}
		}
		
		return output;
	}
	private static String[] addNullsBefore(String[] s, int table2ColSize) {
		String[] output = new String[s.length+table2ColSize];
		
		for(int i = 0; i < table2ColSize; i++) {
			output[i] = "Null";
		}
		System.arraycopy(s, 0, output, table2ColSize, s.length);
		return output;
	}
	private static void makeTable(File file, ArrayList<String[]> table) {
		try {
			Scanner in = new Scanner(file);
			
			String myLine;
			String[] lineArray;
			
			//activeTable.get(row)[0] is personID, 1 is activityName
			//peopleTable.get(row)[1] is personID, 1 is name
			
			while(in.hasNextLine()) {
				myLine = in.nextLine();
				table.add(myLine.split(","));
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}//makeTable
	public static ArrayList<String[]> leftJoin(ArrayList<String[]> table1, ArrayList<String[]> table2) {
		//find the key
		ArrayList<String[]> output = new ArrayList<String[]>();
		int headerRow = 0;
		int table1KeyCol = -1;
		int table2KeyCol = -1;
		boolean found = false;
		
		for(int i = 0; i < table1.get(headerRow).length && table1KeyCol == -1 && table2KeyCol == -1; i++) {
			for(int j = 0; j < table2.get(headerRow).length && table1KeyCol == -1 && table2KeyCol == -1; j++) {
				if(table1.get(headerRow)[i].equals(table2.get(headerRow)[j])) {
					table1KeyCol = i;
					table2KeyCol = j;
				}
			}
		}
		
		//do the left join
		
		for(int row1 = 1; row1 < table1.size(); row1++) {
			found = false;
			for(int row2 = 1; row2 < table2.size(); row2++) {
				if(table1.get(row1)[table1KeyCol].equals(table2.get(row2)[table2KeyCol])) {
					output.add(arrayJoiner(table1.get(row1),table2.get(row2)));
					found = true;
				}
			}
			if(!found) {
				output.add(addNullsAfter(table1.get(row1), table2.get(0).length));
			}
		}
		return output;
	}
	public static String[] addNullsAfter(String[] s, int table2ColSize) {
		String[] output = new String[s.length+table2ColSize];
		System.arraycopy(s, 0, output, 0, s.length);
		for(int i = s.length; i < output.length; i++) {
			output[i] = "Null";
		}
		return output;
	}
	public static String[] arrayJoiner(String[] a, String[] b) {
		int size = a.length+b.length;
		String[] out = new String[size];
		System.arraycopy(a, 0, out, 0, a.length);
		System.arraycopy(b, 0, out, a.length, b.length);
		return out;
	}
	private static void printTable(ArrayList<String[]> table)
	{
		for(int i = 0; i < table.size(); i++)
		{
			for(int j = 0; j < table.get(i).length; j++) {
				if(j != table.get(i).length-1) {
					System.out.print(table.get(i)[j]+",");
				} else {
					System.out.print(table.get(i)[j]+"\n");
				}
			}
		}
	}
}//class

//check if it has 1 matching column
//if(countRow(matchingColumns) == 1) {
//	int table1KeyCol = matchingColumns[0][0];
//	int table2KeyCol = matchingColumns[0][1];
//	for(int row1 = 1; row1 < table1.size(); row1++) {
//		for(int row2 = 1; row2 < table2.size(); row2++) {
//			if(table1.get(row1)[table1KeyCol].equals(table2.get(row2)[table2KeyCol])) {
//				output.add(arrayJoiner(table1.get(row1),table2.get(row2)));
//				found = true;
//			}
//		}
//	}