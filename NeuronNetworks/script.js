const fs = require('fs');

function readFile(fileName) {
    return fs.readFileSync(fileName, 'utf8', function (err, data) {
        if (err) {
            throw err;
        }
        return data;
    });
}

function writeFile(fileName, text) {
    fs.writeFile(fileName, text, function (err) {
        if (err) {
            throw err;
        }
    });
}

function first() {
    let data = readFile('input.txt');
    data = data.split('\r').join('').split(' ').join('');

    const bnf = {};
    const usedVertexes = [];

    const lines = data.split('\n');
    lines.forEach(line => {
        const [vertex, vertexesStr] = line.split(':');
        bnf[vertex] = [];
        const vertexes = vertexesStr.split(',');
        vertexes.forEach(v => {
            if (v !== '') {
                let num;
                if (!usedVertexes[v]) {
                    usedVertexes[v] = 1;
                    num = 1;
                } else {
                    num = ++usedVertexes[v];
                }
                bnf[vertex].push(`${num}-${v}`);
            } else {
                bnf[vertex].push('_');
            }
        });
    });

    let answer = '';
    for (const vertex in bnf) {
        answer += `${vertex} : `;

        const bnfVer = bnf[vertex];
        bnfVer.forEach((v, i) => {
            const postfix = i === bnfVer.length - 1 ? '' : ', ';
            answer += `${v}${postfix}`;
        });
        answer += '\n';
    }

    writeFile('output.txt', answer);

    //Todo: проверка файла
}

first();