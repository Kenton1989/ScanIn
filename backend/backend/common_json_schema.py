import jsonschema

AUTHENTICATION_OBJECT = {
    "type": "object",
    "properties": {
        "username": {"type": "string"},
        "hashed_password": {"type": "string"},
    },
    "required": ["username", "hashed_password"],
    "additionalProperties": False,
}

EMPTY_JSON_OBJECT = {
    "type": "object",
    "properties": {},
}

IMAGE_STRING = {
    "type": "string",
    "contentEncoding": "base64",
    "contentMediaType": "image/jpeg"
}
