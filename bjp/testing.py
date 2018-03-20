import requests
import csv

url="https://docs.google.com/spreadsheets/d/e/2PACX-1vT9hOpxCOKgYMPpZa70mZnBxoV2sLs6MZzczHehjmFCW9w-e4WUY8s17pyy1mgbkWgOP8qZCDLHhil5/pub?output=csv";
code=requests.get(url)
# print (code.text)
# cr = csv.reader(code.text)
text = code.iter_lines()
reader = csv.reader(text, delimiter=',')
for row in reader:
    print row