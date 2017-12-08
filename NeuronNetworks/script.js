const fs = require('fs');

class Graph {
    //Вход: данные из файла
    constructor(data, isBnf = false) {
        this.graphArray = null;
        this.graph = null;
        this.graphEdges = null;

        if (data) {
            if (!isBnf) {
                // [ [ 'a', [ 'b' ] ], [ 'b', [ 'c' ] ], [ 'c', [ 'a' ] ] ]
                this.graphArray = this.parseGraphList(data);
                // { a: [ 'b' ], b: [ 'c' ], c: [ 'a' ] } }
                this.graph = this.createGraph(this.graphArray);
            } else {
                this.graphEdges = this.parseBnfGraphToEdges(data);
                this.graph = this.getGraphFromEdges(this.graphEdges);
            }
        }
    }

    getXML() {
        let xml = '<?xml version="1.0" encoding="UTF-8"?>';
        xml += '<graph>';
        for (let v in this.graph) {
            for (let i = 0; i < this.graph[v].length; i++) {
                const u = this.graph[v][i];
                xml += `
<edge>
    <startVertex>${v}</startVertex>
    <endVertev>${u}</endVertev>
</edge>`;
            }
        }
        xml += '</graph>';
        return xml;
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
        let answer = this.getFunctionFromBNF(this.graphEdges.sinks, this.graphEdges.edges);
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

        let funcObj = null;
        try {
            funcObj = JSON.parse(dataCopy);
        } catch (e) {
            throw 'Не корректное количество скобочек!';
        }

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

    getGraphFromEdges(bnf) {
        const graph = {};
        const edges = bnf.edges;
        for (let i = 0; i < edges.length; i++) {
            const x = edges[i][0], y = edges[i][1];
            if (!graph[x]) {
                graph[x] = [];
            }
            if (!graph[y]) {
                graph[y] = [];
            }
            graph[x].push(y);
        }
        return graph;
    }

    //Вход: данные из файла
    parseGraphList(data) {
        data = data.split('\r').join('').split(' ').join('');

        const lines = data.split('\n');

        const objGraph = [];
        lines.forEach(line => {
            if (line.length > 0) {
                const [vertex, vertexesStr] = line.split(':');
                const vertexes = vertexesStr.split(',');
                objGraph.push([vertex, vertexes]);
            }
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
                    if (!answer[p].find(v => v === vertex)) {
                        answer[p].push(vertex);
                    }
                });
            }
            this.fromFuncToGraph(funcObj[vertex], answer);
        }
    }

    //Проверка все ли вершины определены
    isCorrectVertexes() {
        let err = '';
        if (this.graph) {
            let j = 1;
            for (const v in this.graph) {
                for (let i = 0; i < this.graph[v].length; i++) {
                    if (!this.graph[this.graph[v][i]]) {
                        err += `Вершина ${this.graph[v][i]} - не определена. (строка ${j})\n`;
                    }
                }
                j++;
            }
        }
        if (err.length > 0) {
            throw err;
        }
    }

    createGraph(graphArray) {
        const graph = {};
        for (let i = 0; i < graphArray.length; i++) {
            graph[graphArray[i][0]] = graphArray[i][1].filter(v => v !== '');
        }
        this.isCorrectVertexes();
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

//Main

console.log('Введите номер задания:');
console.log('1: Создание графа (Вход: Список списков графа)');
console.log('2: Создание функции по графу (Вход: БНФ графа)');
console.log('3: Вычисление значения функции на графе (Вход: Список списков графа, операции)');
console.log('4: Построение графа по функции, переданной в префиксной скобочной записи (Вход: Функция графа)');
// console.log('5: Построение многослойной нейронной сети');
// console.log('6: Реализация метода обратного распространения ошибки для многослойной НС');
console.log('P.S. Входной файл: input.txt, Выходной файл: output.txt, Файл с операциями для 3 задания в operations.txt');

// const stdin = process.openStdin();
// stdin.addListener('data', (data) => {
//     const fileInput = 'input.txt', fileOutput = 'output.txt';
//     const fileXML = 'graph.xml';
//
//     const fileData = readFile(fileInput);
//     const action = data.toString().trim();
//
//     try {
//         switch (action) {
//             case '1': {
//                 const graph = new Graph(fileData);
//                 writeFile(fileOutput, graph.getBnf());
//                 writeFile(fileXML, graph.getXML());
//                 break;
//             }
//             case '2': {
//                 const graph = new Graph(fileData, true);
//                 if (!graph.isAcyclic()) {
//                     writeFile(fileOutput, 'Данный граф содержит циклы!');
//                 } else {
//                     writeFile(fileOutput, graph.getFunctionFromBnf());
//                 }
//                 break;
//             }
//             case '3': {
//                 const graph = new Graph(fileData);
//                 writeFile(fileOutput, graph.calcFunctionFromGraph('operations.txt'));
//                 break;
//             }
//             case '4': {
//                 const graph = new Graph();
//                 const bnf = graph.getBnfFromFunction(fileData);
//                 graph.graph = graph.getGraphFromEdges(graph.parseBnfGraphToEdges(bnf));
//
//                 writeFile(fileOutput, graph.getBnfFromFunction(fileData));
//                 writeFile(fileXML, graph.getXML());
//                 break;
//             }
//             case '5': {
//                 break;
//             }
//             case '6': {
//                 break;
//             }
//             default: {
//                 console.log('Некорректные данные!');
//             }
//         }
//     } catch (e) {
//         writeFile(fileOutput, e);
//     }
//
//     process.exit(0);
// });

class Layer {
    constructor(weights = [[]]) {
        this.weights = weights;
    }

    setX(inputs = []) {
        this.inputs = inputs;
    }

    setY(outputs = []) {
        this.ouputs = outputs;
    }

    calcY(x, w = this.weights) {
        this.setX(x);

        const y = [];
        for (let j = 0; j < w[0].length; j++) {
            let yj = 0;
            for (let k = 0; k < x.length; k++) {
                yj += w[k][j] * x[k];
            }
            y.push(Layer.f(yj));
        }

        this.setY(y);
        return y;
    }

    calcDeltas(deltas, w = this.weights) {
        this.deltas = deltas;

        const deltasPrLayer = [];
        for (let i = 0; i < w.length; i++) {
            let d = 0;
            for (let j = 0; j < deltas.length; j++) {
                d += w[i][j] * deltas[j];
            }
            deltasPrLayer.push(d);
        }
        return deltasPrLayer;
    }

    recalcW(deltas = this.deltas, w = this.weights, x = this.inputs, y = this.ouputs) {
        for (let i = 0; i < w.length; i++) {
            for (let j = 0; j < w[i].length; j++) {
                const learnRate = 0.5;
                w[i][j] += learnRate * deltas[j] * Layer.df(y[j]) * x[i];
            }
        }
    }

    static f(x, alpha = 2) {
        return 1 / (1 + Math.exp(-1 * x * alpha));
        // return x >= 0.5 ? 1 : 0;
    }

    static df(x, alpha = 2) {
        return alpha * Layer.f(x) * (1 - Layer.f(x));
    }
}

class Network {
    constructor(weights = [[]]) {
        this.layers = weights.map(w => new Layer(w));
    }

    calcY(inputs, layers = this.layers) {
        let y = inputs.slice(0);
        layers.forEach(layer => {
            y = layer.calcY(y).slice(0);
        });
        return y;
    }

    train(inputs, outputs, rounds = 10) {
        for (let k = 0; k < rounds; k++) {
            let error = 0;
            inputs.forEach((input, i) => {
                let locErr = 0;
                for (let jj = 0; jj < 1; jj++) {
                    const y = this.calcY(input);
                    const deltas = y.map((yj, j) => outputs[i][j] - yj);
                    locErr = deltas.reduce((res, d) => {
                        res += 0.5 * d * d;
                        return res;
                    }, 0);
                    if (locErr > 0.01) {
                        this.calcDeltas(deltas);
                        this.recalcW();
                    } else {
                        break;
                    }
                }
                error += locErr;
            });
            console.log(k, error / inputs.length);
        }
    }

    calcDeltas(deltas, layers = this.layers) {
        let d = deltas.slice(0);
        for (let i = layers.length - 1; i >= 0; i--) {
            d = layers[i].calcDeltas(d).slice(0);
        }
    }

    recalcW(layers = this.layers) {
        layers.forEach(layers => layers.recalcW());
    }

    static parseMatrix(data) {
        data = data.split('  ').join(' ').split('\r').join('');
        const lines = data.split('\n');
        const m = [];
        lines.forEach(line => {
            if (line !== '') {
                const ms = line.split(':')[1].trim();
                m.push(JSON.parse(`[${ms}]`));
            }
        });
        return m;
    }

    static parseTestSamples(data) {
        data = data.split(' ').join('').split('\r').join('');
        const lines = data.split('\n');
        const res = {
            inputs: [],
            outputs: [],
        };
        lines.forEach(line => {
            if (line !== '') {
                const sp = line.split('->');
                res.inputs.push(sp[0]);
                res.outputs.push(sp[1]);
            }
        });
        return res;
    }
}

// const n = new Network(ws);
// console.log(n.calcY(input));
// const inp = [[1, 0], [0, 1], [0, 0], [1, 1]];
// const out = [[1], [1], [0], [0]];
// n.train(inp, out, 1000);
// console.log(parseMatrix(readFile('input.txt')));
// console.log(parseTestSamples(readFile('input.txt')));