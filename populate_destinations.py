#!/usr/bin/env python3
"""
create_destinations_with_images.py - Modified for bulk data population.

Generates 5 real-world destinations using Pexels images, and
95 fictional destinations using mock image/video URLs.
Total: 100 destinations, each with 5 images and 5 mock videos.
"""

import requests
import uuid
from io import BytesIO
import sys
import time
import random
from faker import Faker # requires: pip install faker

# ========== CONFIG ==========
PEXELS_API_KEY = "RPIgagJW4vMqV1C9hgq1y3lUTQfAzvx2fSMZ53ky1dvgYLJOEREedUSU"
BASE_URL = "http://localhost:8083/api/v1"
PEXELS_SEARCH_URL = "https://api.pexels.com/v1/search"
UPLOAD_SINGLE_ENDPOINT = f"{BASE_URL}/media/upload/single"
DESTINATIONS_ENDPOINT = f"{BASE_URL}/destinations"

# Target counts
TOTAL_DESTINATIONS_NEEDED = 100
IMAGES_PER_DESTINATION = 5
VIDEOS_PER_DESTINATION = 5

# Sample real destinations (will use Pexels)
REAL_DESTINATIONS = [
    {"name": "Thiès", "region": "West Africa", "airport": ("airport-123", "DSS")},
    {"name": "Dakar", "region": "West Africa", "airport": ("airport-001", "DSS")},
    {"name": "Lagos", "region": "West Africa", "airport": ("airport-002", "LOS")},
    {"name": "Nairobi", "region": "East Africa", "airport": ("airport-003", "NBO")},
    {"name": "Accra", "region": "West Africa", "airport": ("airport-004", "ACC")},
]

# Initialize Faker for mock data
fake = Faker()

# ========== Helpers (Unmodified or Minor changes) ==========

def get_pexels_images(query, per_page=IMAGES_PER_DESTINATION):
    """Fetches image URLs from Pexels."""
    headers = {"Authorization": PEXELS_API_KEY}
    params = {"query": query, "per_page": per_page, "orientation": "landscape"} # Added orientation
    r = requests.get(PEXELS_SEARCH_URL, headers=headers, params=params, timeout=30)
    r.raise_for_status()
    data = r.json()
    photos = data.get("photos", [])
    urls = []
    for p in photos:
        src = p.get("src", {})
        # Prefer larger size but keep a fallback
        url = src.get("large2x") or src.get("large") or src.get("original")
        if url:
            urls.append(url)
    return urls[:per_page]


def upload_single_image_from_url(image_url, folder):
    """Downloads image and uploads to your backend."""
    # Download the image bytes
    r = requests.get(image_url, timeout=30)
    r.raise_for_status()
    content = r.content

    # Build multipart/form-data
    filename = f"{uuid.uuid4()}.jpg"
    files = {
        "file": (filename, BytesIO(content), "image/jpeg")
    }
    data = {"folder": folder}

    resp = requests.post(UPLOAD_SINGLE_ENDPOINT, files=files, data=data, timeout=60)
    resp.raise_for_status()
    # Expecting ApiResponse with .data being the image URL
    json_resp = resp.json()
    data_field = json_resp.get("data")
    if isinstance(data_field, str):
        return data_field
    if isinstance(data_field, dict) and "url" in data_field:
        return data_field["url"]
    if isinstance(data_field, list) and len(data_field) > 0:
        return data_field[0]
    raise RuntimeError(f"Unexpected upload response: {json_resp}")


# ========== New Mock Data Generator ==========

def generate_mock_destinations(count):
    """Generates a list of fictional destinations using Faker."""
    mock_destinations = []
    regions = ["North America", "South America", "Europe", "Asia", "Oceania"]

    for i in range(count):
        city = fake.city()
        airport_code = fake.unique.bothify(text='???').upper()
        mock_destinations.append({
            "name": f"Mock {city}",
            "region": random.choice(regions),
            "airport": (f"mock-airport-{i:03d}", airport_code),
        })
    return mock_destinations


def get_mock_media_urls(name):
    """Generates mock URLs for images and videos."""
    # Fictional URLs based on the destination name for easy tracing
    mock_image_urls = [
        f"https://mock-cdn.com/images/{name.lower().replace(' ', '-')}_{i}.jpg"
        for i in range(1, IMAGES_PER_DESTINATION + 1)
    ]
    mock_video_urls = [
        f"https://mock-cdn.com/videos/{name.lower().replace(' ', '-')}_{i}.mp4"
        for i in range(1, VIDEOS_PER_DESTINATION + 1)
    ]
    return mock_image_urls, mock_video_urls

# ========== Updated Payload Creation ==========

def create_destination_payload(dest, image_urls, video_urls):
    """Creates the payload for the destination POST request."""
    # List of highlights
    all_highlights = ["Culture", "Cuisine", "Nature", "Beaches", "History", "Adventure", "Shopping"]
    num_highlights = random.randint(3, 5)
    highlights = random.sample(all_highlights, num_highlights)

    return {
        "name": dest["name"],
        "region": dest["region"],
        "nearestAirportId": dest["airport"][0],
        "nearestAirportCode": dest["airport"][1],
        "description": f"Explore the vibrant {dest['name']}, a jewel in {dest['region']}. {fake.text(max_nb_chars=100)}",
        "highlights": highlights,
        "images": image_urls,
        "videos": video_urls, # Included videos
        "virtualTourUrl": "",
        "bestSeason": random.choice(["Year-round", "November to May", "June to October"]),
        "averageStayDuration": random.randint(3, 10),
        "popularityScore": round(random.uniform(5.0, 9.9), 1),
        "latitude": round(random.uniform(-90.0, 90.0), 4),
        "longitude": round(random.uniform(-180.0, 180.0), 4),
        "createdBy": "admin-user-123",
        "active": True # Add the active field from your example JSON
    }


def create_destination(payload):
    """Sends the POST request to create the destination."""
    r = requests.post(DESTINATIONS_ENDPOINT, json=payload, timeout=30)
    r.raise_for_status()
    response = r.json()
    # expects { "success": true, "data": { "id": "<uuid>", ... } ... }
    data = response.get("data")
    if isinstance(data, dict) and "id" in data:
        return data["id"]
    if isinstance(data, str):
        return data
    raise RuntimeError(f"Unexpected create response: {response}")


# ========== Main Execution Block ==========
def main():
    print(f"Starting population script for {TOTAL_DESTINATIONS_NEEDED} destinations...")

    # Generate mock data
    mock_count = TOTAL_DESTINATIONS_NEEDED - len(REAL_DESTINATIONS)
    MOCK_DESTINATIONS = generate_mock_destinations(mock_count)
    ALL_DESTINATIONS = REAL_DESTINATIONS + MOCK_DESTINATIONS

    for dest in ALL_DESTINATIONS:
        is_real = dest in REAL_DESTINATIONS

        try:
            print(f"\n--- Processing {'REAL' if is_real else 'MOCK'} destination: {dest['name']} ---")
            uploaded_urls = []
            mock_video_urls = []

            if is_real:
                # 1) Get Pexels image URLs and upload (for real destinations)
                pexels_urls = get_pexels_images(dest["name"], IMAGES_PER_DESTINATION)

                if not pexels_urls:
                    print(f"⚠️ No pexels images for {dest['name']}, skipping.")
                    continue
                print(f"Found {len(pexels_urls)} images on Pexels.")

                # Upload each image to backend using upload/single
                folder = f"destinations/{str(uuid.uuid4())}"  # unique folder per destination
                for idx, img_url in enumerate(pexels_urls, start=1):
                    print(f"Uploading image {idx}/{len(pexels_urls)} for {dest['name']}")
                    uploaded_url = upload_single_image_from_url(img_url, folder)
                    print(f" -> uploaded: {uploaded_url}")
                    uploaded_urls.append(uploaded_url)
                    time.sleep(0.5) # small delay to be gentle

                # Mock videos for real destinations too
                _, mock_video_urls = get_mock_media_urls(dest["name"])

            else:
                # 1) Use mock URLs (for fictional destinations)
                print(f"Using {IMAGES_PER_DESTINATION} mock images and {VIDEOS_PER_DESTINATION} mock videos.")
                uploaded_urls, mock_video_urls = get_mock_media_urls(dest["name"])

            # 2) Create destination with media URLs
            payload = create_destination_payload(dest, uploaded_urls, mock_video_urls)
            dest_id = create_destination(payload)
            print(f"✅ Created destination '{dest['name']}' id={dest_id} with {len(uploaded_urls)} images and {len(mock_video_urls)} videos")

        except Exception as e:
            print(f"❌ Error processing {dest['name']}: {e}", file=sys.stderr)
            # continue with next destination

    print("\nDone.")

if __name__ == "__main__":
    # Ensure you have the 'faker' library installed: pip install faker
    main()