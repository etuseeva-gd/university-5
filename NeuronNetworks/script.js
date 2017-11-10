const js2xmlparser = require("js2xmlparser");
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
    fs.writeFileSync(fileName, text, function (err) {
        if (err) {
            throw err;
        }
    });
}

// function objToXML(name, obj) {
//     const xml = js2xmlparser.parse(name, obj);
//     writeFile(`xml_${name}.txt`, xml);
// }

function first() {
    //Todo: проверки валидности

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
    // objToXML('graph', bnf);
}

function readGraph(fileName) {
    let data = readFile(fileName);
    data = data.split('\r').join('').split(' ').join('');

    const graph = {};

    const lines = data.split('\n');
    lines.forEach(line => {
        const [vertex, vertexesStr] = line.split(':');
        graph[vertex] = vertexesStr.split(',').filter(v => v !== '');
    });

    return graph;
}

function second() {
    //Todo: проверки валидности

    let data = readFile('input1.txt');
    data = data.split('\r').join('').split(' ').join('');

    const graphEdges = [];
    const sinks = [];

    const lines = data.split('\n');
    lines.forEach(line => {
        if (line !== '') {
            const [vertex, vertexesStr] = line.split(':');
            const vertexes = vertexesStr.split(',');
            vertexes.forEach(v => {
                if (v === '_') {
                    sinks.push(vertex);
                } else {
                    graphEdges.push([vertex, v.split('-')[1]]);
                }
            });
        }
    });

    let answer = {};
    sinks.forEach(s => {
        answer[s] = getVertexesForSink(s, graphEdges);
    });

    answer = JSON.stringify(answer);

    const signs = ['"', ':', '{', '}'],
        newSigns = ['', '', '(', ')'];

    signs.forEach((s, i) => {
        answer = answer.split(s).join(newSigns[i]);
    });

    writeFile('output.txt', `Graph${answer}`);
    console.log(answer);
}

function getVertexesForSink(sink, graphEdges) {
    const answer = {};
    for (let i = 0; i < graphEdges.length; i++) {
        if (graphEdges[i][1] === sink) {
            let vertex = graphEdges[i][0];
            graphEdges.splice(i--, 1);
            answer[vertex] = getVertexesForSink(vertex, graphEdges);
        }
    }
    return answer;
}

first();
second();