<script setup>
    import router from '../router'
    import Header from '../components/Header.vue'
    import * as XLSX from 'xlsx'
    import { useUploadStore } from '../store/uploadStore.js'

    const upload = useUploadStore()

    function aoSelecionarArquivo(event) {
        const file = event.target.file?.[0]
        if (!file) return

        upload.selecionarArquivo(file)
        upload.erros = []
    }

    async function processarArquivo() {
        if (!upload.arquivo) {
            upload.erros = ['Selecione uma planilha antes de processar.']
            return
        }

        const nome = upload.arquivo.name.toLowerCase()
        const valido = ['.xlsx', '.xls', '.csv'].some((ext) => nome.endsWith(ext))

        if (!valido) {
            upload.erros = ['Formato inválido!']
            return
        }
    }
</script>

<template>
    <Header />

    <main>
        <!-- Section para importar arquivos CSV, XLSX -->
        <section class="px-8 md:px-[400px] py-16 lg:py-24 bg-[#f8fafc] pt-12 gap-12">
            <h1 class="text-4xl md:text-4xl font-extrabold text-palette-chumbo leading-tight mb-1 max-w-[650px]" >Upload do arquivo</h1>
            <h2 class="text-base md:text-lg font-light text-palette-chumbo leading-tight mb-6" >Arraste e solte para carregar o arquivo intantaneamentearquivo</h2>

            <div class="flex justify-center items-center flex-col bg-[#ffffff] rounded-2xl border-2 border-dashed border-[#52796F]">
                <img src="../assets\upload_images\upload_icon.png" alt="Imagem de Upload" class="mt-20">
                <h2 class="font-light text-palette-chumbo">Arraste e solte para carregar o arquivo ou <span class="text-palette-mid-green font-bold underline">Escolha um Arquivo</span></h2>
                <h3 class="mb-20">Formatos aceitos: XLSX, CSV</h3>
            </div>
        </section>
    </main>
</template>

<style scoped>


</style>