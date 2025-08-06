from flask import Flask, render_template, jsonify
import json
import os

app = Flask(__name__)

LOG_FILE = "/home/ubuntu/cowrie_logs/cowrie.json"

@app.route("/")
def index():
    return render_template("index.html")

@app.route("/logs")
def get_logs():
    logs = []
    if os.path.exists(LOG_FILE):
        with open(LOG_FILE, "r") as f:
            for line in f:
                line = line.strip()
                if not line:
                    continue
                try:
                    logs.append(json.loads(line))
                except json.JSONDecodeError:
                    # Skip invalid lines
                    logs.append({"log": line})
    return jsonify(logs[-50:])  # last 50 logs


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)

