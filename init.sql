CREATE DATABASE IF NOT EXISTS staticanalyzerdb;

USE staticanalyzerdb;

CREATE TABLE user (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id), 
    UNIQUE (username)
);

CREATE TABLE project (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    source_code LONGBLOB NOT NULL,
    config TEXT NOT NULL,
    analyse_result MEDIUMTEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

INSERT INTO user (username, password) VALUES 
    ('test0', 'test0@staticanalyzer'),
    ('test1', 'test1@staticanalyzer'),
    ('test2', 'test2@staticanalyzer'),
    ('test3', 'test3@staticanalyzer'),
    ('test4', 'test4@staticanalyzer'),
    ('test5', 'test5@staticanalyzer'),
    ('test6', 'test6@staticanalyzer'),
    ('test7', 'test7@staticanalyzer'),
    ('test8', 'test8@staticanalyzer');

INSERT INTO project (user_id, source_code, config, analyse_result) VALUES
    (
        (SELECT id FROM user WHERE username='test0'),
        FROM_BASE64('
            H4sIAAAAAAAAA+2Y34qjMBTGvRZ8h8PMTctCaxJNQMvCzjPsC0Qru4Ja0QizlHn3TWztH2mRYTd2
            d+b8box6gpbT78sX8yotum22dizia4QQ5khE6F8eBxwSBJRwSpmgjk9oKAIHQpsvNdC1SjYATiHL
            ZCuL7F7d1P3/lPzYf7n6ae0ZU/0nPj31P2Sh7j/TQwd8a290wSfv/3PdyB+lhF2VZp7ruXmlQGWt
            +kYWZvi6hD00meqaCl7hC9AY3i6K6GIZP/oXIH/CoP/kkfoP+Fn/gvT6Fwz1Pwdj/aeFbFv4rsX9
            AnvPrbukyNPIc0EzyP7ltjeQ3huuCo0/eO4besQ/SynzapXWtc1nTOk/oOFJ/zw06z/lhKD+5+D5
            uADAk06AT557Pk+uzzf5rlVNJsuvQ0wwf53F0nP3B9G3ahtF6a5TsNmc0oEZ9zeyalvEAOs1+PfK
            ycIf1+tycig/OFIS35icrI6mdGs6vT/h9usx7VePbsqMtE1qde9neNf+j+vrRC//Ie7/5sD0X65S
            q8+Y7D9n5/wXcuP/jOH+bxbG/n+9uTuZ+zHmkfhzuePHx+g/sRwAJ/Uv6EX+8/v8xznqfw7Gee+g
            /z5tRdEQk8Y2wNAGPgpG/1lZq18WU+C78l8g+u8/+hLmPwRBEARBEARBEAT5S/wG8te5/QAoAAA=
        '),
        'Framework{level=0}',
        '{
            "code":0,
            "msg":"ok",
            "algAnalyseResults":[
                {
                    "analyseType":"UseBeforeDef",
                    "code":0,
                    "msg":"done",
                    "fileAnalyseResults":{
                        "main.cpp":{
                            "analyseResults":[
                                {
                                    "startLine":10,
                                    "startColumn":7,
                                    "endLine":10,
                                    "endColumn":8,
                                    "severity":"Warning",
                                    "message":"Variable p used before definition."
                                }
                            ]
                        },
                        "src/a.c":{
                            "analyseResults":[
                                {
                                    "startLine":5,
                                    "startColumn":5,
                                    "endLine":5,
                                    "endColumn":6,
                                    "severity":"Info",
                                    "message":"Variable q may be undefined."
                                }
                            ]
                        }
                    }
                },
                {
                    "analyseType":"ArrayOutOfBound",
                    "code":0,
                    "msg":"done",
                    "fileAnalyseResults":{
                        "main.cpp":{
                            "analyseResults":[
                                {
                                    "startLine":5,
                                    "startColumn":6,
                                    "endLine":5,
                                    "endColumn":16,
                                    "severity":"Error",
                                    "message":"arr[index] is out of bound."
                                }
                            ]
                        },
                        "src/a.c":{
                            "analyseResults":[
                                {
                                    "startLine":1,
                                    "startColumn":3,
                                    "endLine":1,
                                    "endColumn":6,
                                    "severity":"Warning",
                                    "message":"idx may be out of bound."
                                }
                            ]
                        }
                    }
                }
            ]
        }'
    ),
    (
        (SELECT id FROM user WHERE username='test0'),
        FROM_BASE64('
            H4sIAAAAAAAAA+2Y3Y6bMBCFuUbiHUa7N4kqJbb5sQRRpe4ztA9gCGqRgCAw1VZR3r02hPwgUhS1
            kO3ufDeYMJYdTc7hOEkepfU2XhsTQhScc32l3CWX1w6DOg6jHmM2ZwahzOWOAe6Um+qoKylKACMV
            WbgVaXyrbuz5f0py7L9Y/ZhsjbH+U8JO/XdtV/XfVkMDyGQ7uuCD9/+5KMX3TMAuj2LLtMwklyDj
            Sn6hCz18XcIeyljWZQ6v8AlYAIeLIrZYBo/+Bsjf0Ok/fKT+He+sf04b/XMb9T8Hff1Hqagq+KrE
            /QJ7yyzqME0i3zJB0cn+ZdgbaOMNV4XaHyzzgB7xZslEkq+iophyjTH9u47T6J85xGMe0fnPIw7q
            fw6ejy8AeFIJ8Mkyz/fh9f0m2VWyjEX2uYsJ+qezWFrmvhV9Jbe+H+1qCZvNKR3ocfMgzrdpALBe
            A7lVThekX6/KaVveOlIYDEwOV0dTGprObk8Y3p7dTjgbmRhaU/RnNnO/5UmeyETIeAs/RZmIMFWm
            enh0j/9EVUaTnv00d53/Gv2r17+L57850P0Xq2jSNUb779nn/Od62v9tG89/s9D3/+vD3cncjzGP
            Bm/czpA70foPJw6Ao/rn7CL/uU3+U8dA1P8M9PNeq/8mbfl+F5P6NmCjDbwXtP7jrJC/JkyBd+U/
            hzf//6iPMP8hCIIgCIIgCIIgyD/iN64cEMYAKAAA
        '),
        'Framework{level=0}',
        '{
            "code":0,
            "msg":"ok",
            "algAnalyseResults":[
                {
                    "analyseType":"UninitializedVariable",
                    "code":0,
                    "msg":"done",
                    "fileAnalyseResults":{
                        "main.cpp":{
                            "analyseResults":[
                                {
                                    "startLine":13,
                                    "startColumn":5,
                                    "endLine":13,
                                    "endColumn":9,
                                    "severity":"Warning",
                                    "message":"Uninitialized variable test."
                                }
                            ]
                        }
                    }
                }
            ]
        }'
    );
