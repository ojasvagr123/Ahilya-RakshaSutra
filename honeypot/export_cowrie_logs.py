import json
import re

INPUT_LOG = "/home/ubuntu/cowrie_logs/cowrie.log"
OUTPUT_JSON = "/home/ubuntu/cowrie_logs/cowrie.json"

# Regex patterns to extract info from Cowrie logs
LOG_PATTERN = re.compile(
    r"^(?P<timestamp>\S+)\s+\[.*?(?P<event>HoneyPotSSHTransport|cowrie\.ssh.*?|twisted\.conch.*?)\]\s*(?P<message>.*)$"
)
IP_PATTERN = re.compile(r"(\d+\.\d+\.\d+\.\d+)")

def parse_log_line(line: str):
    """Parse a single Cowrie log line into structured JSON"""
    match = LOG_PATTERN.match(line)
    if not match:
        return None

    data = match.groupdict()
    # Extract IP if present in message
    ip_match = IP_PATTERN.search(data["message"])
    data["source_ip"] = ip_match.group(1) if ip_match else None
    return {
        "timestamp": data["timestamp"],
        "source_ip": data["source_ip"],
        "event": data["event"],
        "message": data["message"]
    }

def export_logs():
    structured_logs = []
    with open(INPUT_LOG, "r") as infile:
        for line in infile:
            parsed = parse_log_line(line.strip())
            if parsed:
                structured_logs.append(parsed)

    with open(OUTPUT_JSON, "w") as outfile:
        for log in structured_logs[-200:]:  # Keep last 200 logs
            outfile.write(json.dumps(log) + "\n")

if __name__ == "__main__":
    export_logs()

