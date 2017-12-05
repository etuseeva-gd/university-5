// const js2xmlparser = require("js2xmlparser");
const fs = require('fs');

class Graph {
    //Граф подается как из файла
    constructor(data) {
        const graphObj = parseGraphList(data);

        this.graph = {};
        for (let i = 0; i < graphObj.length; i++) {
            this.graph[graphObj[i][0]] = graphObj[i][1].filter(v => v !== '');
        }
    }

    createGraph() {

    }

    //Если ацикличный, то вернет true
    isAcyclic() {
        const p = {}, color = {};
        this.cycle = false;
        for (const v in this.graph) {
            p[v] = -1;
            color[v] = 0;
        }
        for (const v in this.graph) {
            this.dfs(v, color, p);
        }
        return !this.cycle;
    }

    dfs(v, color, p) {
        color[v] = 1;
        for (let i = 0; i < this.graph[v].length; i++) {
            let to = this.graph[v][i];
            if (color[to] === 0) {
                p[to] = v;
                this.dfs(to, color, p);
            } else if (color[to] === 1) {
                this.cycle = true;
            }
        }
        color[v] = 2;
    }
}

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

function parseGraphList(data) {
    data = data.split('\r').join('').split(' ').join('');

    const lines = data.split('\n');

    const objGraph = [];
    lines.forEach(line => {
        const [vertex, vertexesStr] = line.split(':');
        const vertexes = vertexesStr.split(',');
        objGraph.push([vertex, vertexes]);
    });

    return objGraph;
}

function parseBnfGraphToEdges(data) {
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

    return {sinks, edges: graphEdges};
}

function getFunctionFromBNF(sinks, edges) {
    const result = {};
    sinks.forEach(s => {
        const tmpEdges = edges.slice(0);
        result[s] = getVertexesForSink(s, tmpEdges);
    });
    return result;
}

function getVertexesForSink(sink, edges) {
    const result = {};
    for (let i = 0; i < edges.length; i++) {
        if (edges[i][1] === sink) {
            let vertex = edges[i][0];
            edges.splice(i--, 1);
            result[vertex] = getVertexesForSink(vertex, edges.slice(0));
        }
    }
    return result;
}

//Входные данные - список списков, выход - БНФ графа
function first(data) {
    //Todo: проверки валидности
    return fromObjToBNF(parseGraphList(data));
}

function fromObjToBNF(objGraph) {
    const bnf = {};
    const usedVertexes = [];

    objGraph.forEach(obj => {
        const [vertex, vertexes] = obj;
        bnf[vertex] = [];
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

    return answer;
}

//Входные данные - БНФ графа, Выход - функция
function second(data) {
    //Todo: проверки валидности
    //Todo: проверка на циклы

    const parseBnf = parseBnfGraphToEdges(data);
    let answer = getFunctionFromBNF(parseBnf.sinks, parseBnf.edges);

    answer = JSON.stringify(answer);

    const signs = ['"', ':', '{', '}'],
        newSigns = ['', '', '(', ')'];

    signs.forEach((s, i) => {
        answer = answer.split(s).join(newSigns[i]);
    });

    return answer;
}

function third() {
    const dataGraph = readFile('input1.txt');

    const bnf = first(dataGraph);
    const parseBnf = parseBnfGraphToEdges(bnf);
    const funcObj = getFunctionFromBNF(parseBnf.sinks, parseBnf.edges);

    console.log(JSON.stringify(funcObj));

    let dataOperations = readFile('input2.txt');
    dataOperations = dataOperations.split('\r').join('').split(' ').join('');

    const operations = {};
    const lines = dataOperations.split('\n');
    lines.forEach(line => {
        const parse = line.split(':');
        if (parse[1] !== '') {
            operations[parse[0]] = parse[1];
        }
    });
    console.log(operations);

    const result = calcResultForFunction(funcObj, operations);

    let answer = '';
    result.forEach(r => {
        answer += `Для ${r.vertex} = ${r.value}\n`;
    });

    writeFile('output.txt', answer);
}

function calcResultForFunction(obj, operations) {
    const result = [];
    for (let o in obj) {
        const numbers = calcNumbersForVertexes(obj[o], operations);
        console.log(numbers);
        const res = calcResultByOperation(operations[o], numbers);
        console.log(res);
        result.push({vertex: o, value: res});
    }
    return result;
}

function calcResultByOperation(operation, numbers) {
    switch (operation) {
        case '+': {
            return numbers[0] + numbers[1];
        }
        case '*': {
            return numbers[0] * numbers[1];
        }
        case '&': {
            return numbers[0] & numbers[1];
        }
        case '|': {
            return numbers[0] | numbers[1];
        }
        case '>>': {
            return numbers[0] >> numbers[1];
        }
        case '<<': {
            return numbers[0] << numbers[1];
        }
        case '!': {
            return ~numbers[0];
        }
    }
}

function calcNumbersForVertexes(obj, operations) {
    let result = [];
    for (let o in obj) {
        if (o >= '0' && o <= '9') {
            result.push(+o);
        } else {
            const numbers = calcNumbersForVertexes(obj[o], operations);
            const res = calcResultByOperation(operations[o], numbers);
            result.push(res);
        }
    }
    return result;
}

//Выходные данные - функция графа, Выход - БНФ графа
function fourth(data) {
    let signs = ['(', '(', ')'],
        newSigns = [':(', '{', '}'];

    signs.forEach((s, i) => {
        data = data.split(s).join(newSigns[i]);
    });
    data = data.substr(1);

    signs = ['{', '}', ':', ','];

    let dataCopy = '';
    for (let i = 0; i < data.length; i++) {
        if (signs.find(s => s === data[i])) {
            dataCopy += data[i];
        } else {
            let j = i + 1;
            while (!signs.find(s => s === data[j])) {
                j++;
            }
            dataCopy += `"${data.substring(i, j)}"`;
            i = j - 1;
        }
    }

    // console.log(data);
    // console.log(dataCopy);

    //Todo: проверка на корректность
    const funcObj = JSON.parse(dataCopy);
    console.log(funcObj);

    const listEdges = {};
    fromFuncToGraph(funcObj, listEdges);
    console.log(listEdges);

    const objGraph = [];
    for (const v in listEdges) {
        if (listEdges[v].length === 0) {
            listEdges[v].push('');
        }
        objGraph.push([v, listEdges[v]]);
    }

    return fromObjToBNF(objGraph);
}

function fromFuncToGraph(funcObj, answer) {
    for (const vertex in funcObj) {
        if (!answer[vertex]) {
            answer[vertex] = [];
        }

        if (funcObj[vertex] !== {}) {
            const parents = [];
            for (const v in funcObj[vertex]) {
                parents.push(v);
            }
            parents.forEach(p => {
                if (!answer[p]) {
                    answer[p] = [];
                }
                answer[p].push(vertex);
            });
        }
        fromFuncToGraph(funcObj[vertex], answer);
    }
}

//Main

console.log('Введите номер задания:');
console.log('1: Создание графа');
console.log('2: Создание функции по графу');
console.log('3: Вычисление значения функции на графе');
console.log('4: Построение графа по функции, переданной в префиксной скобочной записи');

// console.log('5: Построение многослойной нейронной сети');
// console.log('6: Реализация метода обратного распространения ошибки для многослойной НС');

console.log('0: Выход');

const stdin = process.openStdin();
stdin.addListener('data', (data) => {
    const fileInput = 'input.txt', fileOutput = 'output.txt';

    let data1 = readFile(fileInput);
    // writeFile(fileOutput, first(data));
    // writeFile(fileOutput, second(data));
    // writeFile(fileOutput, fourth(data));

    // third();

    const g = new Graph(data1);

    const action = data.toString().trim();
    switch (action) {
        case '0': {
            process.exit(0);
            break;
        }
        case '1': {
            first();
            break;
        }
        case '2': {
            second();
            break;
        }
        case '3': {
            third();
            break;
        }
        case '4': {
            fourth();
            break;
        }
        case '5': {
            break;
        }
        case '6': {
            break;
        }
        default: {
            console.log('Некорректные данные!');
        }
    }

    console.log("Введите следующее действие:");
});