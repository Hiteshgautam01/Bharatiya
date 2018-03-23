import requests
import json
import MySQLdb
import csv

db = MySQLdb.connect(host="localhost",    # your host, usually localhost
                     user="root",         # your username
                     passwd="Desh@1996WAYNE",  # your password
                     db="bjp", use_unicode=True, charset="utf8")        # name of the data base

# # you must create a Cursor object. It will let
# #  you execute all the queries you need
cur = db.cursor()

API_KEY = "AIzaSyBE9mbp0nr232TJ6vvbQ4Hxt-3aoktK2Yw"

url="https://docs.google.com/spreadsheets/d/e/2PACX-1vT9hOpxCOKgYMPpZa70mZnBxoV2sLs6MZzczHehjmFCW9w-e4WUY8s17pyy1mgbkWgOP8qZCDLHhil5/pub?output=csv";
code=requests.get(url)

text = code.iter_lines()
# for row in text:
# 	print (row)
# print (text)
# file = open(text, "rt", "utf-8")
reader = csv.reader(text, delimiter=',')
# print (reader.text)
a = []
for row in reader:
	if len(row[2])>1:
		a.append(row[2])

for i in a:
	url = "https://www.googleapis.com/youtube/v3/search?key={}&channelId={}&part=snippet,id&order=date&maxResults=2".format(API_KEY, i)
	output_json = requests.get(url)
	s = json.loads(output_json.text)
	# cur.execute("INSERT INTO youtube_posts (message, url, image_link, source_name, source, timedate) VALUES (%s, %s, %s, %s, %s);", (s['items'][0]['snippet']['title'], s['items'][0]['id']['videoId'], '', s['items'][0]['snippet']['channelTitle'], 1, s['items'][0]['snippet']['publishedAt']))
	if len(s['items'])>0:
		query = "INSERT INTO youtube_posts (message, url, image_link, source_name, source, timedate) SELECT * FROM (SELECT %s, %s, %s, %s, %s, %s) AS tmp WHERE NOT EXISTS (SELECT id FROM youtube_posts WHERE url = %s) LIMIT 1;"
		cur.execute(query, (s['items'][0]['snippet']['title'], s['items'][0]['id']['videoId'], '', s['items'][0]['snippet']['channelTitle'], '1', s['items'][0]['snippet']['publishedAt'], s['items'][0]['id']['videoId']))

db.commit()


db.close()