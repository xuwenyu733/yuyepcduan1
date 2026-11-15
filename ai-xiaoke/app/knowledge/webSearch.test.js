import test from 'node:test'
import assert from 'node:assert/strict'
import { parseSo360Results, webSearch } from './webSearch.js'

test('360 results retain the real summary and publisher URL', () => {
  const html = `<li class="res-list" data-result-type="richsum"><h3 class="res-title"><a href="https://www.so.com/link?m=abc" data-mdurl="https://example.org/pond?a=1&amp;b=2">水质资料</a></h3><div class='res-rich so-rich-news'><p></p><span class="res-list-summary" data-result-summary>数据库外的<em>溶解氧</em>相关背景资料原文摘要。</span></div></li>`
  assert.deepEqual(parseSo360Results(html), [{ title: '水质资料', snippet: '数据库外的 溶解氧 相关背景资料原文摘要。', url: 'https://example.org/pond?a=1&b=2', source: '360' }])
})

test('a search result without a summary does not invent one from its title', async () => {
  const originalFetch = globalThis.fetch
  const originalKey = process.env.BING_SEARCH_KEY
  delete process.env.BING_SEARCH_KEY
  globalThis.fetch = async url => ({ ok: true, text: async () => String(url).includes('so.com')
    ? '<li class="res-list"><h3><a href="https://example.org/pond">水产养殖资料</a></h3></li>' : '' })
  try {
    const result = await webSearch('水质')
    assert.equal(result.items.length, 1)
    assert.equal(result.items[0].snippet, '')
  } finally {
    globalThis.fetch = originalFetch
    if (originalKey === undefined) delete process.env.BING_SEARCH_KEY
    else process.env.BING_SEARCH_KEY = originalKey
  }
})
