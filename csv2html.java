/* csv2html.java
 *
 * Usage: java csv2html infile.csv outfile.html
 *
 * Convert a CSV file into an HTML table.
 * 
 * Inspired by csv2html.py from chapter 2 of
 * Mark Summerfield's book "Programming in Python 3" 2/e.
 */

import java.io.*;

class AugString {
    String str;
    boolean lastLine;
}

class Lines {
    // read a line from fin.
    public static AugString readLine(FileInputStream fin) {
        AugString str = new AugString();
        int c = -1;

        str.str = "";
        str.lastLine = false;

        try {
            do {
                c = fin.read();
                if((c != -1) && (c != '\n')) {
                    str.str = str.str + Character.toString(c);
                }
            } while((c != -1) && (c != '\n'));
        } catch (IOException exc) {
            System.out.println("I/O error");
        }

        if(c == -1) {
            str.lastLine = true;
        }

        return(str);
    }
    
    // write a line to fout
    public static void writeLine(FileOutputStream fout, String str) {
        try {
            for(int i=0; i < str.length();i++) {
                fout.write(str.charAt(i));
            }
            fout.write('\n');
        } catch (IOException exc) {
            System.out.println("I/O error");
        }
    }
}


class csv2html {
    public static void main(String args[]) {
        String open_str = "<table border=\'1\'>";
        String close_str = "</table>";
        FileInputStream fin;
        FileOutputStream fout;
        AugString line;
        String line_html;
        int count = 0;
        String color;

        if (args.length != 2) {
            System.out.println(
                "Usage: java csv2html infile.csv outfile.html");
            return;
        }

        // open the file
        try {
            fin = new FileInputStream(args[0]);
            fout = new FileOutputStream(args[1]);
        } catch (FileNotFoundException exc) {
            System.out.println("Error: file not found");
            return;
        }

        // print opening string
        Lines.writeLine(fout,open_str);

        // read and process line-by-line
        while(true) {
            line = Lines.readLine(fin);
            
            if (count == 0) {
                color = "lightgreen";
            } else if (count%2 == 0) {
                color = "white";
            } else {
                color = "lightyellow";
            }
            
            line_html = makeline(line.str,color);

            if( line_html != "" ) {
                Lines.writeLine(fout,line_html);
            }
            
            count += 1;
            if (line.lastLine == true) {
                break;
            }
        }

        // print closing string
        Lines.writeLine(fout,close_str);

        // close the file
        try {
            if (fin != null) {
                fin.close();
            }
            if (fout != null) {
                fout.close();
            }
        } catch (IOException exc) {
            System.out.println("Error closing files");
        }
    }

    public static String makeline(String line, String color) {
        String str = "";
        String strpre = "";
        String strpost = "";
        String[] fields;

        strpre += "<tr bgcolor=\'";
        strpre += color;
        strpre += "\'>\n";
        strpost += "</tr>\n";

        fields = extract_fields(line);

        for(int i=0; i<fields.length; i++) {
            if (fields[i] == "") {
                str += "<td></td>\n";
            } else {
                str += "<td>";
                str += fields[i];
                str += "</td>\n";
            }
        }

        if( str == "") {
            return "";
        }

        return (strpre + str + strpost);
    }


    public static String escape_html(int c) {
        String str = "";
        if( c == '&') {
            str += "&amp;";
        } else if (c == '<') {
            str += "&lt;";
        } else if (c == '>') {
            str += "&gt;";
        } else {
            str += Character.toString(c);
        }
        return str;
    }

    public static String[] extract_fields(String line) {
        String[] fields = new String[0];
        String field = "";
        int quote = -1;

        for( int i = 0; i < line.length(); i++) {
            int c = line.charAt(i);

            // add a character unless it is a quote
            if ((c == '\'') || (c == '\"')) {
                if (quote == -1) {
                    quote = c;
                } else if (quote == c) {
                    quote = -1;
                } else {
                    field += escape_html(c);
               }
                continue;
            }

            if ((quote == -1) && (c == ',')) { // end of a field
                fields = addString(fields,field);
                field = "";
            } else {
                field += escape_html(c);
            }
        }
        if( field != "") {
            fields = addString(fields,field);
        }

        return fields;
    }

    public static String[] addString(String[] fields, String field) {
        String[] fields2 = new String[fields.length+1];

        for(int i = 0;i<fields.length; i++) {
            fields2[i] = fields[i];
        }

        fields2[fields.length] = field;

        return fields2;
    }
}

