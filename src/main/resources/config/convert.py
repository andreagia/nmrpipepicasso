import sys
import csv
import re

header = ['Peak_no','x','y']
data =[]
with open(sys.argv[1], encoding = 'utf-8') as f:
    in1 = f.readlines()

for i in in1:
    #print(i)
    if re.search(r"^\s+[0-9]+", i):
        #print(i)
        data.append([i.split()[0],i.split()[5],i.split()[6]])

with open('out.csv', 'w', encoding='UTF8', newline='') as f:
    writer = csv.writer(f)

    # write the header
    writer.writerow(header)

    # write multiple rows
    writer.writerows(data)

