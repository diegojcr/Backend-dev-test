package stsa.kotlin_htmx.pages

import io.ktor.server.html.*
import kotlinx.html.*
import stsa.kotlin_htmx.pages.HtmlElements.rawCss

/**
 * See https://ktor.io/docs/server-html-dsl.html#templates for more information
 */
class MainTemplate<T : Template<FlowContent>>(private val template: T, val pageTitle: String) : Template<HTML> {

    val mainSectionTemplate = TemplatePlaceholder<T>()
    val headerContent = Placeholder<FlowContent>()

    override fun HTML.apply() {
        lang = "en"
        attributes["data-theme"] = "light"

        head {
            title { +"HTMX and KTor <3 - $pageTitle" }
            meta { charset = "UTF-8" }
            meta {
                name = "viewport"
                content = "width=device-width, initial-scale=1"
            }
            meta {
                name = "description"
                content = "Hello"
            }
            link {
                rel = "icon"
                href = "/static/favicon.ico"
                type = "image/x-icon"
                sizes = "any"
            }
            link {
                rel = "stylesheet"
                href = "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css"
            }
            script(src = "https://www.googletagmanager.com/gtag/js?id=G-30QSF4X9PW") {}
            script {
                unsafe {
                    raw(
                        """
                          window.dataLayer = window.dataLayer || [];
                          function gtag(){dataLayer.push(arguments);}
                          gtag('js', new Date());

                          gtag('config', 'G-30QSF4X9PW');
                        """.trimIndent()
                    )
                }
            }
            script(src = "https://unpkg.com/htmx.org@2.0.3") {}
            script(src = "https://unpkg.com/htmx-ext-json-enc@2.0.1/json-enc.js") {}
            script(src = "https://unpkg.com/htmx-ext-preload@2.0.1/preload.js") {}
            script(src = "https://unpkg.com/htmx-ext-sse@2.2.2/sse.js") {}

            style {
                rawCss(
                    """                        
                        .htmx-indicator{
                            opacity:0;
                            transition: opacity 500ms ease-in;
                        }
                        .htmx-request .htmx-indicator{
                            opacity:1
                        }
                        .htmx-request.htmx-indicator{
                            opacity:1
                        }                                        
                        
                        .box {
                            border: 1px solid red;
                            border-radius: 0.5em;
                            text-align: center;
                            padding: 1em;                    
                        }
                                            
                        section {
                            margin-bottom: 2em;
                        }
                                                                                                    
                        nav {
                            background-color: #333;
                            width: 100%;
                            border-radius: 8px;
                            font-size: 0.8em;
                            padding-left: 1em;
                            padding-right: 1em;
                            margin-bottom: 1em;
                        
                            & ul {
                                list-style: none;
                                display: flex;
                                justify-content: space-evenly;
                                align-items: center;
                                margin: 0 auto;
                                padding: 0;
                                width: 100%;
                            }
                        
                            & li {
                                display: flex;
                                align-items: center;
                                margin: 0;
                                white-space: nowrap;
                            }
                        
                            & .separator {
                                color: #666;
                            }
                        
                            & a {
                                color: white;
                                text-decoration: none;
                                font-family: Arial, sans-serif;
                                transition: color 0.3s ease;
                            }
                        
                            & a:hover {
                                color: #66c2ff;
                            }
                        }
                        
                        .form-error {
                            color: red;
                        }
    
                        .htmx-modified {
                          animation: highlight-fade 3s ease-out;
                        }
                        
                        @keyframes highlight-fade {
                          from {
                            background-color: #d07777;
                          }
                          to {
                            background-color: transparent;
                          }
                        }
                        .skins-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
                            gap: 1.5rem;
                            padding: 1rem;
                            width: 100%;
                            box-sizing: border-box;
                        }
                        
                        .skin-card {
                            background: white;
                            border-radius: 8px;
                            padding: 1.2rem;
                            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                            transition: all 0.3s ease;
                            display: flex;
                            flex-direction: column;
                            height: 100%;
                            box-sizing: border-box;
                        }
                        
                        .skin-card:hover {
                            transform: translateY(-5px);
                            box-shadow: 0 6px 12px rgba(0,0,0,0.15);
                        }
                        .skin-card h3 {
                            margin: 0.5rem 0;
                            font-size: 1.1rem;
                            color: #333;
                            line-height: 1.3;
                        }
                        
                        .skin-card p {
                            margin: 0.3rem 0;
                            font-size: 0.9rem;
                            color: #666;
                        }
                        
                        @media (max-width: 768px) {
                            .skins-grid {
                                grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
                            }
                        }
                        
                        @media (max-width: 480px) {
                            .skins-grid {
                                grid-template-columns: 1fr;
                            }
                        }
                        .crates-list {
                            color: #666;
                            margin-top: auto;
                            padding-top: 0.5rem;
                            border-top: 1px dashed #eee;
                        }
                        
                        .crates-list span {
                            word-break: break-all;
                        }
                        .search-container {
                            margin-bottom: 2rem;
                            position: relative;
                        }
                        
                        .search-container form {
                            display: flex;
                            gap: 0.5rem;
                        }
                        
                        .search-container input {
                            flex-grow: 1;
                            padding: 0.75rem;
                            border: 1px solid #ddd;
                            border-radius: 4px;
                            font-size: 1rem;
                        }
                        
                        .search-container button {
                            padding: 0.75rem 1.5rem;
                            background-color: #4CAF50;
                            color: white;
                            border: none;
                            border-radius: 4px;
                            cursor: pointer;
                        }
                        
                        .htmx-indicator {
                            display: none;
                            position: absolute;
                            right: 1rem;
                            top: 50%;
                            transform: translateY(-50%);
                            color: #666;
                        }
                        
                        .htmx-request .htmx-indicator {
                            display: block;
                        }
                        
                        .htmx-request form button {
                            visibility: hidden;
                        }
                        form {
                            max-width: 400px;
                            margin: 2rem auto;
                            padding: 2rem;
                            border: 1px solid #ddd;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        }
                        
                        form div {
                            margin-bottom: 1rem;
                        }
                        
                        form label {
                            display: block;
                            margin-bottom: 0.5rem;
                        }
                        
                        form input {
                            width: 100%;
                            padding: 0.5rem;
                            border: 1px solid #ddd;
                            border-radius: 4px;
                        }
                        
                        form button {
                            background: #4CAF50;
                            color: white;
                            border: none;
                            padding: 0.75rem 1.5rem;
                            border-radius: 4px;
                            cursor: pointer;
                        }
                    """.trimIndent()
                )
            }
        }
        body {            // This is inherited so means we use JSON as a default for all communication
            attributes["hx-ext"] = "json-enc"

            div {
                style = "max-width: 90vw; margin: auto;"

                // Logo
                header {
                    h1 { +"Startrack Demos" }

                    nav {
                        ul {
                            li { a(href = "/") { +"Home" } }
                            li { span("separator") { +"🚀" } }
                            li { a(href = "/skins") { +"Skins" } }
                            li { span("separator") { +"🚀" } }
                            li { a(href = "/agents") { +"Agents" } }
                            li { span("separator") { +"🚀" } }
                            li { a(href = "/crates") { +"Crates" } }
                            li { span("separator") { +"🚀" } }
                            li { a(href = "/keys") { +"Keys" } }
                        }
                    }

                    div {
                        insert(headerContent)
                    }
                }

                // Main content
                main {
                    id = "mainContent"
                    insert(template, mainSectionTemplate)
                }

                footer {
                    +""
                }

                script {
                    unsafe {
                        raw(
                            """
                            document.body.addEventListener('htmx:afterSettle', function(evt) {
                                // The updated element is directly available in evt.detail.elt
                                const updatedElement = evt.detail.elt;
                                updatedElement.classList.add('htmx-modified');
                            });
                        """.trimIndent()
                        )
                    }
                }
            }
        }
    }
}

// The two below is mainly to cater for two different sub-templates
class SelectionTemplate : Template<FlowContent> {
    val selectionPagesContent = Placeholder<FlowContent>()

    override fun FlowContent.apply() {
        style {
            rawCss(
                """
                    #choices {
                        display: grid; /* Enables grid layout */
                        grid-template-columns: repeat(auto-fit, minmax(15em, 1fr)); /* Adjust the number of columns based on the width of the container */
                        /* Key line for responsiveness: */
                        gap: 20px; /* Adjust the spacing between items */
            
                        a {
                            display: block;
                        }
                    }                    
                """.trimIndent()
            )
        }
        insert(selectionPagesContent)
    }
}

/**
 * This is an empty template to allow us to enforce specifying something
 *
 * There is probably a better way to do this
 */
class EmptyTemplate : Template<FlowContent> {
    val emptyContentWrapper = Placeholder<FlowContent>()

    override fun FlowContent.apply() {
        insert(emptyContentWrapper)
    }
}