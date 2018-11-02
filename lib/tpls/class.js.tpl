// Don't modified this file, it's auto created by jar2proxy

'use strict';

/* eslint-disable */
/* jshint ignore:start */
module.exports = {
  '{{ class.canonicalName }}': {
    {%- if class.enumType %}
    'isEnum': true,
    {%- endif %}
    {%- for fieldDesc in class.fields %}
    {% set comment = fieldDesc.commentText | comment(4) %}
    {%- if comment -%}
    {{ comment }}
    {% endif -%}
    '{{ fieldDesc.fieldName }}': {
      'type': '{{ fieldDesc.canonicalName }}',
      {%- if fieldDesc.defaultValue != null %}
      {%- if fieldDesc.defaultValue == 'new Date()' %}
      get defaultValue() {
        return new Date();
      },
      {%- else %}
      'defaultValue': {{ fieldDesc.defaultValue| stringify}},
      {%- endif %}
      {%- endif %}
      {%- if fieldDesc.constantValue != null %}
      'constantValue': {{ fieldDesc.constantValue| stringify}},
      {%- endif %}
      {%- if fieldDesc.typeAliasIndex != null %}
      'typeAliasIndex': {{ fieldDesc.typeAliasIndex }},
      {%- endif %}
      {%- if fieldDesc.isArray %}
      'isArray': true,
      {%- endif %}
      {%- if fieldDesc.arrayDepth %}
      'arrayDepth': {{ fieldDesc.arrayDepth }},
      {%- endif %}
      {%- if fieldDesc.isEnum %}
      'isEnum': true,
      {%- endif %}
      {%- if fieldDesc.mapType %}
      'isMap': true,
      {%- endif %}
      {%- if fieldDesc.listType %}
      'isList': true,
      {%- endif %}
      {%- if fieldDesc.isStatic %}
      'isStatic': true,
      {%- endif %}
      {%- if fieldDesc.isFinal %}
      'isFinal': true,
      {%- endif %}
      {%- if fieldDesc.isTransient %}
      'isTransient': true,
      {%- endif %}
      {%- if fieldDesc.abstractClass %}
      'abstractClass': '{{ fieldDesc.canonicalName }}',
      {%- endif %}
      {%- if fieldDesc.generic %}
      'generic': [
        {%- for gen in fieldDesc.generic %}
        {{ gen | stringify }},
        {%- endfor %}
      ]
      {%- endif %}
    },
    {%- endfor %}
  },
};
/* jshint ignore:end */
/* eslint-enable */
