import requests
import json
a = ["UCDS9hpqUEXsXUIcf0qDcBIA",
"UCrwE8kVqtIUVUzKui2WVpuQ",
"UCIvaYmXn910QMdemBG3v1pQ",
"UCoDiyU0xjSXJhmKJCqLeCNQ",
"UC9fA5HwFkQyGUzh6OX74shw",
"UCwqusr8YDwM-3mEYTDeJHzw",
"UCz4wetSaxA-UJDdSQ-OuFJg",
"UCpfOz2YAg52V5D2394v5BXg",
"UC9vgi80SCBp0o9Sk7d-rNuQ",
"UCEP_YnSbTAzg2bjtnCJMi_A",
"UCAYi-MPk4suFqAzaFgDrZGw",
"UCG9eujEZMa3cvv1foTr0pnw",
"UCx1Lqozh7LSG9NTSgE-kcvA",
"UCsuTXG0k_Cy9xBzKMzdOyuw",
"UCpC3DFj9mRBNOk_gnYk8dSQ"]
API_KEY = "AIzaSyBE9mbp0nr232TJ6vvbQ4Hxt-3aoktK2Yw"
# CHANNEL_ID = "UCgAvalds_Q4FLwt2K_L57Ug"
for i in a:
	url = "https://www.googleapis.com/youtube/v3/search?key={}&channelId={}&part=snippet,id&order=date&maxResults=20".format(API_KEY, i)
	output_json = requests.get(url)
	s = json.loads(output_json.text)