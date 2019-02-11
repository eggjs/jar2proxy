// Don't modified this file, it's auto created by @ali/jar2proxy
{%- for dep in proxyProfile.dependencies -%}
{#- 引入依赖 #}
{{ dep | getImport() }}
{%- endfor %}

{{ proxyProfile.commentText | comment(2) }}
interface {{ proxyProfile.canonicalName | splitLast('.') | upperFirst }} {
{%- for item in proxyProfile.methods %}

  {{ item.commentText | comment(2) }}
  {{ item.methodName }}{%- if item.isOverloading %}${{ item.uniqueId }}{%- endif %}(
    {%- for arg in item.params %}
    {{ arg.paramName }}: {{ arg.canonicalName | splitLast('.') | upperFirst | getType(arg) }},
    {%- endfor %}
  ): Promise<{{ item.returnType.canonicalName | splitLast('.') | upperFirst | getType(item.returnType) }}>;

{%- endfor %}
}

declare module '{{ eggFramework }}' {
  export interface IProxy {
    {{proxyName | lowerFirst}}: {{ proxyProfile.canonicalName | splitLast('.') | upperFirst }};
  }
}

