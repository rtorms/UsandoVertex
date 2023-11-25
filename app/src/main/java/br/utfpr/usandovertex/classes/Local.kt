package br.utfpr.usandovertex.classes

class Local {

    private var id: String? = null
    private var nome: String? = null
    private var descricao: String? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private var endereco: String? = null
    private var urlImagem: String? = null

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getNome(): String? {
        return nome
    }

    fun setNome(nome: String?) {
        this.nome = nome
    }

    fun getDescricao(): String? {
        return descricao
    }

    fun setDescricao(descricao: String?) {
        this.descricao = descricao
    }

    fun getLatitude(): String? {
        return latitude
    }

    fun setLatitude(latitude: String?) {
        this.latitude = latitude
    }

    fun getLongitude(): String? {
        return longitude
    }

    fun setLongitude(longitude: String?) {
        this.longitude = longitude
    }

    fun getEndereco(): String? {
        return endereco
    }

    fun setEndereco(endereco: String?) {
        this.endereco = endereco
    }

    fun getUrlImagem(): String? {
        return urlImagem
    }

    fun setUrlImagem(urlImagem: String?) {
        this.urlImagem = urlImagem
    }
}