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
