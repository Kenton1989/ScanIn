CREATE TABLE person_info (
    PID VARCHAR(32) NOT NULL,
    name VARCHAR(32) UNIQUE NOT NULL,
    hashed_password CHAR(64) NOT NULL,
    activated BOOLEAN NOT NULL DEFAULT 1,
    is_admin BOOLEAN NOT NULL DEFAULT 0,

    PRIMARY KEY (PID)
);

CREATE TABLE session_info (
    SID INT NOT NULL AUTO_INCREMENT,
    creator VARCHAR(64),
    session_name VARCHAR(512),
    venue VARCHAR(512),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    next_session INT,

    CHECK (start_time < end_time),
    PRIMARY KEY(SID),
    FOREIGN KEY(next_session) REFERENCES session_info(SID)
        ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE attendance (
    AID INT NOT NULL AUTO_INCREMENT,
    PID VARCHAR(32) NOT NULL,
    SID INT NOT NULL,

    check_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    checkIn BOOLEAN,

    PRIMARY KEY (AID),
    FOREIGN KEY (PID) REFERENCES person_info(PID)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (SID) REFERENCES session_info(SID)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE authorized_attendee (
    PID VARCHAR(32) NOT NULL,
    SID INT NOT NULL,

    PRIMARY KEY(PID, SID),
    FOREIGN KEY (PID) REFERENCES person_info(PID)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (SID) REFERENCES session_info(SID)
        ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE facial_vector (
    PID VARCHAR(32) NOT NULL,

    v000 DOUBLE NOT NULL,
    v001 DOUBLE NOT NULL,
    v002 DOUBLE NOT NULL,
    v003 DOUBLE NOT NULL,
    v004 DOUBLE NOT NULL,
    v005 DOUBLE NOT NULL,
    v006 DOUBLE NOT NULL,
    v007 DOUBLE NOT NULL,
    v008 DOUBLE NOT NULL,
    v009 DOUBLE NOT NULL,
    v010 DOUBLE NOT NULL,
    v011 DOUBLE NOT NULL,
    v012 DOUBLE NOT NULL,
    v013 DOUBLE NOT NULL,
    v014 DOUBLE NOT NULL,
    v015 DOUBLE NOT NULL,
    v016 DOUBLE NOT NULL,
    v017 DOUBLE NOT NULL,
    v018 DOUBLE NOT NULL,
    v019 DOUBLE NOT NULL,
    v020 DOUBLE NOT NULL,
    v021 DOUBLE NOT NULL,
    v022 DOUBLE NOT NULL,
    v023 DOUBLE NOT NULL,
    v024 DOUBLE NOT NULL,
    v025 DOUBLE NOT NULL,
    v026 DOUBLE NOT NULL,
    v027 DOUBLE NOT NULL,
    v028 DOUBLE NOT NULL,
    v029 DOUBLE NOT NULL,
    v030 DOUBLE NOT NULL,
    v031 DOUBLE NOT NULL,
    v032 DOUBLE NOT NULL,
    v033 DOUBLE NOT NULL,
    v034 DOUBLE NOT NULL,
    v035 DOUBLE NOT NULL,
    v036 DOUBLE NOT NULL,
    v037 DOUBLE NOT NULL,
    v038 DOUBLE NOT NULL,
    v039 DOUBLE NOT NULL,
    v040 DOUBLE NOT NULL,
    v041 DOUBLE NOT NULL,
    v042 DOUBLE NOT NULL,
    v043 DOUBLE NOT NULL,
    v044 DOUBLE NOT NULL,
    v045 DOUBLE NOT NULL,
    v046 DOUBLE NOT NULL,
    v047 DOUBLE NOT NULL,
    v048 DOUBLE NOT NULL,
    v049 DOUBLE NOT NULL,
    v050 DOUBLE NOT NULL,
    v051 DOUBLE NOT NULL,
    v052 DOUBLE NOT NULL,
    v053 DOUBLE NOT NULL,
    v054 DOUBLE NOT NULL,
    v055 DOUBLE NOT NULL,
    v056 DOUBLE NOT NULL,
    v057 DOUBLE NOT NULL,
    v058 DOUBLE NOT NULL,
    v059 DOUBLE NOT NULL,
    v060 DOUBLE NOT NULL,
    v061 DOUBLE NOT NULL,
    v062 DOUBLE NOT NULL,
    v063 DOUBLE NOT NULL,
    v064 DOUBLE NOT NULL,
    v065 DOUBLE NOT NULL,
    v066 DOUBLE NOT NULL,
    v067 DOUBLE NOT NULL,
    v068 DOUBLE NOT NULL,
    v069 DOUBLE NOT NULL,
    v070 DOUBLE NOT NULL,
    v071 DOUBLE NOT NULL,
    v072 DOUBLE NOT NULL,
    v073 DOUBLE NOT NULL,
    v074 DOUBLE NOT NULL,
    v075 DOUBLE NOT NULL,
    v076 DOUBLE NOT NULL,
    v077 DOUBLE NOT NULL,
    v078 DOUBLE NOT NULL,
    v079 DOUBLE NOT NULL,
    v080 DOUBLE NOT NULL,
    v081 DOUBLE NOT NULL,
    v082 DOUBLE NOT NULL,
    v083 DOUBLE NOT NULL,
    v084 DOUBLE NOT NULL,
    v085 DOUBLE NOT NULL,
    v086 DOUBLE NOT NULL,
    v087 DOUBLE NOT NULL,
    v088 DOUBLE NOT NULL,
    v089 DOUBLE NOT NULL,
    v090 DOUBLE NOT NULL,
    v091 DOUBLE NOT NULL,
    v092 DOUBLE NOT NULL,
    v093 DOUBLE NOT NULL,
    v094 DOUBLE NOT NULL,
    v095 DOUBLE NOT NULL,
    v096 DOUBLE NOT NULL,
    v097 DOUBLE NOT NULL,
    v098 DOUBLE NOT NULL,
    v099 DOUBLE NOT NULL,
    v100 DOUBLE NOT NULL,
    v101 DOUBLE NOT NULL,
    v102 DOUBLE NOT NULL,
    v103 DOUBLE NOT NULL,
    v104 DOUBLE NOT NULL,
    v105 DOUBLE NOT NULL,
    v106 DOUBLE NOT NULL,
    v107 DOUBLE NOT NULL,
    v108 DOUBLE NOT NULL,
    v109 DOUBLE NOT NULL,
    v110 DOUBLE NOT NULL,
    v111 DOUBLE NOT NULL,
    v112 DOUBLE NOT NULL,
    v113 DOUBLE NOT NULL,
    v114 DOUBLE NOT NULL,
    v115 DOUBLE NOT NULL,
    v116 DOUBLE NOT NULL,
    v117 DOUBLE NOT NULL,
    v118 DOUBLE NOT NULL,
    v119 DOUBLE NOT NULL,
    v120 DOUBLE NOT NULL,
    v121 DOUBLE NOT NULL,
    v122 DOUBLE NOT NULL,
    v123 DOUBLE NOT NULL,
    v124 DOUBLE NOT NULL,
    v125 DOUBLE NOT NULL,
    v126 DOUBLE NOT NULL,
    v127 DOUBLE NOT NULL,

    PRIMARY KEY (PID),
    FOREIGN KEY (PID) REFERENCES person_info(PID)
        ON UPDATE CASCADE ON DELETE CASCADE
);