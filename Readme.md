# CSV to DMN converter
## How can it benefit you?
The purpose of this program is to convert a CSV file to a DMN table and vice versa. This enables you to use e.g. Excel functions to enter information into the DMN table.

## How does it work?
The program is reading the CSV, which has to be in a particular layout, into an object, this is then creating a `DmnModelInstance` (class provided by Camunda), this instance is then exported to a DMN file.
On your .jar file (located in the target folder), which you got from compiling the project with maven, run following command:

`java -jar csv2dmn-converter.jar [-c] [-d] -i inputFile -o outputFile`

Either -c or -d must be provided.\
`-c,--csv-to-dmn          Convert CSV to DMN`\
`-d,--dmn-to-csv          Convert DMN to CSV`\
`-i,--input-file <arg>    Input file (CSV or DMN)`\
`-o,--output-file <arg>   Output file (CSV or DMN)`

## Example
Your CSV-File (values separated with comma) should look like this (further explanation below)
![Origin CSV] [OriginCSV.PNG]

Then on your .jar file in your target folder, which you got from compiling it with Maven run this command:
![Command] [command]

Your outcome should be following:
![Outcome DMN] [outcome-dmn]


## Layout of a compatible file

The CSV needs following format in order to be able to be picked up by the program. All the fields are described afterwards.
Imagine the table below is a CSV opened in Excel.

. | A | B | C | D | E | F | G
--- | --- | --- | --- | --- |--- |--- |---
1 | DmnId | Title | Hit Policy |  |  |  |  
2 | Input | Input | Input | Output | Output | |   
3 | ColumnName1 | ColumnName1 | ColumnName1 | ColumnName1 | ColumnName1 |  | 
4 | string | integer | boolean | date | double |  |  
5 | SomeString | 301 | true | date(“2020-12-31”) | 12.01 | CommentsHere |  
6 | OtherString | 1337 | false | date(“2021-01-01”) | 123.45 | CommentsHere |

## Row 1
### Column A:
This is the ID the DMN gets, you can address your DMN table with this id from a BPMN diagram.

### Column B:
This is the title of the DMN, the title is displayed in the top left corner of the actual DMN table.

### Column C:
This is the section where you can enter the hit policy, if the entered value is not valid, Unique is chosen as the default one.
Here is a list of all the supported HIT policies:
* `Unique`
* `Any`
* `Collect`
* `First`
* `Output Order`
* `Priority` (*not supported by Camunda, only here for drawing purposes*)
* `Rule Order`

## Row 2
A DMN always needs one Input and one Output field. The following paragraph shows you how the program distinguishes between the Input, Output, and the Annotation field.
### Column 'Input'
All columns that have 'Input' in the second row are going to be input columns in the table.

### Column 'Output'
All columns that have 'Output' in the second row are going to be output columns in the table.

### Annotation Column
Every row in DMN can have an annotation, you only have to leave the row blank (e.g. F 2) and then write the annotation next to the row with the values, the program is automatically detecting annotations.

## Row 3
### All Columns
Here you have to state the column name, it is only the column name that will be displayed in the DMN Table, not the ID.
If you want a custom ID, you will have to edit the XML directly either with a text editor or with the Camunda modeler.

## Row 4
### All Columns
This is the line for the data types, you can choose between all data types, that are supported by DMN. 
If you have a typo here, the program won't export the DMN diagram.

## Row 5 and following
### All Columns
Enter your values in these rows, some things to keep in mind are following:
* Strings shouldn't have a ", otherwise they will be displayed like this ""SomeString"".
* Make sure that data types are the same as stated in row 4, otherwise the DMN won't export.
* Row annotations can be made if you just put a string to the last column after the last output value. It has no header or any kind of data type, for an example please have a look at the Column F

## Class description
### CsvPojo
This class is the POJO (Plain Old Java Object), which holds all the information read from the CSV.
If there was a mistake in entering the HIT Policy, "Unique" is the default one.

### CsvReader
This class reads the CSV (path parsed as String to the function) and saves it into the `CsvPojo`, if changes to the layout of the CSV happen, this is the class you have to change as well.

### DmnCreator
This class creates the DMN of the `CsvPojo`, which was previously read by the `CsvReader`. It creates the DMN with the Camunda DMN Model. Basically this functionality it supported out of the box by Camunda.

### DmnFileExporter
This class exports the DMN Model created in the `DmnCreator` to a file (path parsed as String to function).

### InvalidDatatypeException
This class is a custom exception which is thrown, if an invalid data type is parsed through the CSV.

### DmnDataTypes
This class contains all the possible data types that can be entered in the csv.

### SequenceGeneratorImpl
This ensures the uniqueness of the element ids of the DMN.

### DmnToCsvConverter
Converts and exports a DMN to a Csv in the format from above.

## Versions
* `1.0.0`: Initial version.

## Contributors
* Clemens Zumpf
* Dmitrii Pisarenko

## Sponsor
"J-IT" IT-Dienstleistungs GesmbH

Vorgartenstraße 206B

5th floor

A-1020 Vienna

Austria

![JIT logo][logo]

[logo]: docs/jit_logo.png "JIT Logo"
[origin-csv]: docs/OriginCSV.PNG "Origin CSV"
[command]: docs/Command.PNG "Origin CSV"
[outcome-dmn]: docs/OutcomeDMN.PNG "Origin CSV"
