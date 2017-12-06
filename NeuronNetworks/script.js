// const js2xmlparser = require("js2xmlparser");
const fs = require('fs');

class Graph {
    //Граф подается как из файла
    constructor(data, isBnf = false) {
        this.graphArray = null;
        this.graph = null;
        this.graphBnf = null;

        if (data) {
            if (!isBnf) {
                // [ [ 'a', [ 'b' ] ], [ 'b', [ 'c' ] ], [ 'c', [ 'a' ] ] ]
                this.graphArray = this.parseGraphList(data);
                // { a: [ 'b' ], b: [ 'c' ], c: [ 'a' ] } }
                this.graph = this.createGraph(this.graphArray);
            } else {
                this.graphBnf = this.parseBnfGraphToEdges(data);
            }
        }
    }

    getBnf(graph) {
        const graphArray = graph ? graph : this.graphArray;

        const bnf = {};
        const usedVertexes = [];

        graphArray.forEach(obj => {
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

    getFunctionFromBnf() {
        let answer = this.getFunctionFromBNF(this.graphBnf.sinks, this.graphBnf.edges);
        answer = JSON.stringify(answer);

        const signs = ['"', ':', '{', '}'],
            newSigns = ['', '', '(', ')'];

        signs.forEach((s, i) => {
            answer = answer.split(s).join(newSigns[i]);
        });

        return answer;
    }

    calcFunctionFromGraph(operationsFileName) {
        const bnf = this.getBnf();
        const parseBnf = this.parseBnfGraphToEdges(bnf);
        const funcObj = this.getFunctionFromBNF(parseBnf.sinks, parseBnf.edges);

        let dataOperations = readFile(operationsFileName);
        dataOperations = dataOperations.split('\r').join('').split(' ').join('');

        const operations = {};
        const lines = dataOperations.split('\n');
        lines.forEach(line => {
            const parse = line.split(':');
            if (parse[1] !== '') {
                operations[parse[0]] = parse[1];
            }
        });

        const result = this.calcResultForFunction(funcObj, operations);

        let answer = '';
        result.forEach(r => {
            answer += `Для ${r.vertex} = ${r.value}\n`;
        });

        return answer;
    }

    //Вход: данные из файла
    getBnfFromFunction(data) {
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

        //Todo: проверка на корректность

        const funcObj = JSON.parse(dataCopy);

        const listEdges = {};
        this.fromFuncToGraph(funcObj, listEdges);

        const objGraph = [];
        for (const v in listEdges) {
            if (listEdges[v].length === 0) {
                listEdges[v].push('');
            }
            objGraph.push([v, listEdges[v]]);
        }

        return this.getBnf(objGraph);
    }

    //Вход: данные из файла
    parseGraphList(data) {
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

    //Вход: данные из файла
    parseBnfGraphToEdges(data) {
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

    //Вспомогательная функция
    getFunctionFromBNF(sinks, edges) {
        const result = {};
        sinks.forEach(s => {
            const tmpEdges = edges.slice(0);
            result[s] = this.getVertexesForSink(s, tmpEdges);
        });
        return result;
    }

    //Вспомогательная функция
    getVertexesForSink(sink, edges) {
        const result = {};
        for (let i = 0; i < edges.length; i++) {
            if (edges[i][1] === sink) {
                let vertex = edges[i][0];
                edges.splice(i--, 1);
                result[vertex] = this.getVertexesForSink(vertex, edges.slice(0));
            }
        }
        return result;
    }

    //Вспомогательная функция
    calcResultForFunction(obj, operations) {
        const result = [];
        for (let o in obj) {
            const numbers = this.calcNumbersForVertexes(obj[o], operations);
            const res = this.calcResultByOperation(operations[o], numbers);
            result.push({vertex: o, value: res});
        }
        return result;
    }

    //Вспомогательная функция
    calcResultByOperation(operation, numbers) {
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

    //Вспомогательная функция
    calcNumbersForVertexes(obj, operations) {
        let result = [];
        for (let o in obj) {
            if (o >= '0' && o <= '9') {
                result.push(+o);
            } else {
                const numbers = this.calcNumbersForVertexes(obj[o], operations);
                const res = this.calcResultByOperation(operations[o], numbers);
                result.push(res);
            }
        }
        return result;
    }

    //Вспомогательная функция
    fromFuncToGraph(funcObj, answer) {
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
            this.fromFuncToGraph(funcObj[vertex], answer);
        }
    }

    createGraph(graphArray) {
        const graph = {};
        for (let i = 0; i < graphArray.length; i++) {
            graph[graphArray[i][0]] = graphArray[i][1].filter(v => v !== '');
        }
        return graph;
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

    //Обход в глубину
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

//Main

console.log('Введите номер задания:');
console.log('1: Создание графа (Вход: Список списков графа)');
console.log('2: Создание функции по графу (Вход: БНФ графа)');
console.log('3: Вычисление значения функции на графе (Вход: Список списков графа, операции)');
console.log('4: Построение графа по функции, переданной в префиксной скобочной записи (Вход: Функция графа)');
// console.log('5: Построение многослойной нейронной сети');
// console.log('6: Реализация метода обратного распространения ошибки для многослойной НС');
console.log('P.S. Входной файл: input.txt, Выходной файл: output.txt, Файл с операциями для 3 задания в operations.txt')

const stdin = process.openStdin();
stdin.addListener('data', (data) => {
    const fileInput = 'input.txt', fileOutput = 'output.txt';
    const fileData = readFile(fileInput);
    const action = data.toString().trim();
    switch (action) {
        case '1': {
            const graph = new Graph(fileData);
            writeFile(fileOutput, graph.getBnf());
            break;
        }
        case '2': {
            const graph = new Graph(fileData, true);
            writeFile(fileOutput, graph.getFunctionFromBnf());
            break;
        }
        case '3': {
            const graph = new Graph(fileData);
            writeFile(fileOutput, graph.calcFunctionFromGraph('operations.txt'));
            break;
        }
        case '4': {
            const graph = new Graph();
            writeFile(fileOutput, graph.getBnfFromFunction(fileData));
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
    process.exit(0);
});