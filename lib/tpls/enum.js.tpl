/**
 * Don't modified this file, it's auto created by jar2proxy
 * @see {{ canonicalName }}
 */

'use strict';

/* eslint-disable */
/* jshint ignore:start */

/**
 * Module dependencies.
 */
const Enums = require('enums');

{{ commentText | comment }}
module.exports = new Enums([
  {%- for fieldDesc in fields %}
    {%- if fieldDesc.enumValue %}
  {{ fieldDesc.commentText | comment(2) }}
  {{ fieldDesc.enumValue | stringify }},
    {%- endif %}
  {%- endfor %}
]);

/* jshint ignore:end */
/* eslint-enable */
