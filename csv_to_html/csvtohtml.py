#!/usr/bin/python
import sys
import csv

PATH = sys.argv[1]

with open(PATH, 'r', encoding='utf-8', errors='ignore') as infile:
    reader = csv.reader(infile, delimiter=';')
    students = []
    IDs = []
    points = []
    points_sum = None

    for row in reader:
        if reader.line_num == 1:
            for name in row:
                students.append(name)
        elif reader.line_num == 2:
            for ID in row:
                IDs.append(ID)
        else:
            if points_sum is None:
                points_sum = len(students) * [0]
            points_line = len(students) * [0]

            for counter, data in enumerate(row):
                points_sum[counter] += data.count('+')
                points_line[counter] += data.count('+')

            points.append(points_line)
    
with open("output.html", "w") as outfile:
    outfile.write("""<table border="1">""")
    outfile.write("<tr><th>Student</th>")
    for i in range(len(points)):
        outfile.write("<th>" + str(i+1) + "</th>")
    outfile.write("<th>Suma</th></tr>")

    for i, ID in enumerate(IDs):
        outfile.write("<tr><th>" + ID + "</th>")
        for lst in points:
            outfile.write("<th>" + str(lst[i]) + "</th>")
        outfile.write("<th>" + str(points_sum[i]) + "</th></tr>")
    outfile.write("</table>")

   