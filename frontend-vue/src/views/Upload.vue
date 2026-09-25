<script setup>
import Header from "../components/Header.vue";
import { computed, ref } from "vue";
import * as XLSX from "xlsx";
import { useUploadStore } from "../store/uploadStore.js";

const upload = useUploadStore();
const fileInput = ref(null);

const formatarNumero = (valor) => {
    const numero = Number(valor ?? 0);

    if (Number.isNaN(numero)) return "-";

    return new Intl.NumberFormat("pt-BR", {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
    }).format(numero);
};

const formatarData = (valor) => {
    if (!valor) return "-";

    // Se for o número serial do Excel (ex: 45672)
    const numeroSerial = Number(valor);
    if (!Number.isNaN(numeroSerial) && typeof valor !== "boolean" && numeroSerial > 0) {
        // Offset de 25569 dias entre a base do Excel (30/12/1899) e a época Unix (01/01/1970)
        const milissegundosPorDia = 86400 * 1000;
        const dataUtc = new Date(Math.round((numeroSerial - 25569) * milissegundosPorDia));

        if (!Number.isNaN(dataUtc.getTime())) {
            const dia = String(dataUtc.getUTCDate()).padStart(2, "0");
            const mes = String(dataUtc.getUTCMonth() + 1).padStart(2, "0");
            const ano = dataUtc.getUTCFullYear();
            return `${dia}/${mes}/${ano}`;
        }
    }

    // Se já estiver no padrão DD/MM/AAAA
    if (typeof valor === "string" && /^\d{2}\/\d{2}\/\d{4}$/.test(valor.trim())) {
        return valor.trim();
    }

    // Se for formato ISO ou outro formato padrão reconhecido pelo Date
    const data = new Date(valor);
    if (!Number.isNaN(data.getTime())) {
        return data.toLocaleDateString("pt-BR", { timeZone: "UTC" });
    }

    return String(valor);
};

const progresso = computed(() => {
    if (!upload.arquivo) return 0;
    if (upload.carregamento) return 70;
    if (upload.dadosTratados.length) return 100;
    return 20;
});

function abrirSeletor() {
    fileInput.value?.click();
}

async function aoSelecionaArquivo(event) {
    const file = event.target.files?.[0];
    if (!file) return;

    upload.selecionarArquivo(file);
    await processarArquivo();
}

async function processarArquivo() {
    if (!upload.arquivo) {
        upload.erros = ["Selecione uma planilha antes de processar."];
        return;
    }

    const nome = upload.arquivo.name.toLowerCase();
    const valido = [".xlsx", ".xls", ".csv"].some((ext) => nome.endsWith(ext));

    if (!valido) {
        upload.erros = ["Formato inválido. Envie .xlsx, .xls ou .csv."];
        return;
    }

    upload.carregamento = true;
    upload.erros = [];

    try {
        const buffer = await upload.arquivo.arrayBuffer();
        const workbook = XLSX.read(buffer, { type: "array" });
        const planilha = workbook.Sheets[workbook.SheetNames[0]];
        const linhas = XLSX.utils.sheet_to_json(planilha, { defval: "" });

        upload.dadosOriginais = linhas;
        upload.dadosTratados = linhas.map((linha) => ({
            ...linha,
            codigo_cliente: linha.codigo_cliente || linha.codigo || linha.id || "-",
            nome_cliente: linha.nome_cliente || linha.cliente || linha.nome || "Sem nome",
            segmento: upload.normalizarSegmento(linha.segmento),
            nivel_cliente: String(linha.nivel_cliente || linha.nivel || "N/A")
                .trim()
                .toUpperCase(),
        }));
    } catch (error) {
        upload.erros = ["Não foi possível ler a planilha."];
    } finally {
        upload.carregamento = false;
    }
}
</script>

<template>
    <Header />

    <main class="bg-[#f8fafc] md:px-[400px]">
        <!-- Section para importar arquivos CSV, XLSX -->
        <section class="px-8 py-16 lg:py-24  pt-12 gap-12">
            <input ref="fileInput" type="file" @change="aoSelecionaArquivo" />
            <h1 class="text-4xl md:text-4xl font-extrabold text-palette-chumbo leading-tight mb-1 max-w-[650px]">Upload
                do arquivo</h1>
            <h2 class="text-base md:text-lg font-light text-palette-chumbo leading-tight mb-6">Arraste e solte para
                carregar o arquivo intantaneamentearquivo</h2>

            <div class="flex justify-center items-center flex-col bg-[#ffffff] rounded-2xl border-2 border-dashed border-[#52796F] cursor-pointer   "
                @click="abrirSeletor">
                <img src="../assets\upload_images\upload_icon.png" alt="Imagem de Upload" class="mt-20">
                <h2 class="font-light text-palette-chumbo">Arraste e solte para carregar o arquivo ou <span
                        class="text-palette-mid-green font-bold underline" @click="abrirSeletor">Escolha um
                        Arquivo</span></h2>
                <h3 class="mb-20">Formatos aceitos: XLSX, CSV</h3>
            </div>
        </section>

        <div v-if="upload.dadosTratados.length" class=" max-w-full overflow-x-auto pb-10 ">
            <div class="overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm mt-5">
                <table class="w-full border-collapse text-left text-sm text-gray-700">
                    <thead class="bg-[#84A98C] text-[#26351f]">
                        <tr>
                            <th class="px-4 py-3 font-semibold text-white">Código CLiente</th>
                            <th class="px-4 py-3 font-semibold text-white">Nome</th>
                            <th class="px-4 py-3 font-semibold text-white">Nível</th>
                            <th class="px-4 py-3 font-semibold text-white">Cidade</th>
                            <th class="px-4 py-3 font-semibold text-white">Data Contratação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(cliente, index) in upload.dadosTratados.slice(0, 10)" :key="index"
                            class="border-t border-gray-200 hover:bg-[#f9fbf6]">
                            <td class="px-4 py-3">{{ cliente.codigo_cliente || '-' }}</td>
                            <td class="px-4 py-3">{{ cliente.nome_cliente || '-' }}</td>
                            <td class="px-4 py-3">
                                <span :class="cliente.nivel_cliente === 'A'
                                    ? 'bg-[#edf7df] text-[#52761e]'
                                    : cliente.nivel_cliente === 'B'
                                        ? 'bg-[#fef3c7] text-[#9a6700]'
                                        : 'bg-[#dbeafe] text-[#1d4ed8]'"
                                    class="inline-flex rounded-full px-2.5 py-1 text-xs font-semibold">
                                    {{ cliente.nivel_cliente || 'N/A' }}
                                </span>
                            </td>
                            <td class="px-4 py-3">{{ cliente.cidade || cliente.localidade || '-' }}</td>
                            <td class="px-4 py-3">{{ formatarData(cliente.data_contratacao || cliente.data) }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</template>

<style scoped></style>