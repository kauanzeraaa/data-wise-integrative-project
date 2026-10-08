<script setup>
import Header from "../components/Header.vue";
import { useUploadStore } from "../store/uploadStore.js";

const upload = useUploadStore();
</script>

<template>
    <Header />

    <main class="bg-[#f8fafc] min-h-screen px-6 py-12 md:px-[400px]">
        
        <div class="flex justify-between items-end mb-8">
            <div>
                <h1 class="text-3xl font-extrabold text-palette-chumbo">Relatório de Validação</h1>
                <p class="text-sm text-gray-500 mt-1">
                    Arquivo analisado: <strong>{{ upload.nomeArquivo || 'Nenhum' }}</strong> 
                    <span v-if="upload.dataUploadFormatada"> | Data de Upload: {{ upload.dataUploadFormatada }}</span>
                </p>
            </div>
            <RouterLink to="/uploads" class="text-sm font-semibold text-[#52796F] underline">
                Voltar para Upload
            </RouterLink>
        </div>

        <div v-if="!upload.temDados" class="bg-white p-8 rounded-xl border border-gray-200 text-center">
            <p class="text-gray-500">Nenhum dado para exibir. Volte e faça o upload de uma planilha.</p>
        </div>

        <template v-else>
            <!-- Resumo (Cards) -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
                <div class="bg-white p-5 rounded-xl border border-gray-200 shadow-sm text-center">
                    <p class="text-xs text-gray-500 uppercase tracking-wide">Total de Registros</p>
                    <p class="text-3xl font-bold text-palette-chumbo mt-1">{{ upload.totalRegistros }}</p>
                </div>
                <div class="bg-white p-5 rounded-xl border border-[#84A98C] shadow-sm text-center">
                    <p class="text-xs text-[#52796F] uppercase tracking-wide">Registros Válidos</p>
                    <p class="text-3xl font-bold text-[#52796F] mt-1">{{ upload.registrosValidos }}</p>
                </div>
                <div class="bg-white p-5 rounded-xl border border-red-200 shadow-sm text-center">
                    <p class="text-xs text-red-500 uppercase tracking-wide">Registros com Erro</p>
                    <p class="text-3xl font-bold text-red-600 mt-1">{{ upload.registrosComErro }}</p>
                </div>
            </div>

            <!-- Resumo de tipos de erro -->
            <div v-if="upload.totalErros > 0" class="bg-white p-6 rounded-xl border border-gray-200 shadow-sm mb-8">
                <h3 class="text-lg font-bold text-palette-chumbo mb-4">Quantidade de cada tipo de erro:</h3>
                <ul class="space-y-2">
                    <li v-for="(quantidade, tipo) in upload.errosPorTipo" :key="tipo" class="flex justify-between text-sm border-b border-gray-100 pb-2">
                        <span class="text-gray-700">{{ tipo }}</span>
                        <span class="font-bold text-red-600">{{ quantidade }} ocorrência(s)</span>
                    </li>
                </ul>
            </div>

            <!-- Tabela detalhada de erros -->
            <div>
                <h3 class="text-lg font-bold text-palette-chumbo mb-4">Tabela Detalhada de Erros</h3>
                
                <div class="overflow-x-auto rounded-xl border border-gray-200 bg-white shadow-sm">
                    <table class="w-full border-collapse text-left text-sm text-gray-700">
                        <thead class="bg-[#84A98C] text-white">
                            <tr>
                                <th class="px-4 py-3 font-semibold w-24">Linha</th>
                                <th class="px-4 py-3 font-semibold w-48">Campo</th>
                                <th class="px-4 py-3 font-semibold w-48">Tipo de Erro</th>
                                <th class="px-4 py-3 font-semibold">Descrição do Erro</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(erro, index) in upload.erros" :key="index" class="border-t border-gray-100 hover:bg-gray-50">
                                <td class="px-4 py-3 font-bold text-center">{{ erro.linha }}</td>
                                <td class="px-4 py-3">{{ erro.campo }}</td>
                                <td class="px-4 py-3 text-red-600 font-semibold">{{ erro.tipo }}</td>
                                <td class="px-4 py-3 text-gray-600">{{ erro.descricao }}</td>
                            </tr>
                            <tr v-if="upload.erros.length === 0">
                                <td colspan="4" class="px-4 py-8 text-center text-gray-500">
                                    Nenhum erro encontrado! A planilha está perfeita. 🎉
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </template>
    </main>
</template>
