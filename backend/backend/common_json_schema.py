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

DATETIME_STR = {
    "type": "string",
    "format": "date-time",
}


def NULLABLE(schema):
    type_list = [{'type': 'null'}]
    if isinstance(schema, dict):
        type_list.append(schema)
    else:
        try:
            i = iter(type_list)
        except TypeError:
            raise TypeError('schema is not a dict or a iterable of dict')
        else:
            type_list.extend(schema)
    return {'anyOf': type_list}
