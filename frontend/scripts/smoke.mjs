import assert from 'node:assert/strict';
import { createRequire } from 'node:module';
import vm from 'node:vm';
import { build } from 'esbuild';
import { createElement } from 'react';
import { renderToString } from 'react-dom/server';

// Render the initial screen so runtime JSX errors cannot pass unnoticed by a build.
const result = await build({
  entryPoints: ['src/App.jsx'],
  bundle: true,
  write: false,
  platform: 'node',
  format: 'cjs',
  external: ['react'],
});
const compiled = { exports: {} };
const run = vm.runInThisContext(`(function(require, module, exports) { ${result.outputFiles[0].text}\n})`);
run(createRequire(import.meta.url), compiled, compiled.exports);
const html = renderToString(createElement(compiled.exports.default));
assert.match(html, /Khám phá sản phẩm/);
assert.match(html, /Đăng nhập/);
console.log('React initial render: OK');
