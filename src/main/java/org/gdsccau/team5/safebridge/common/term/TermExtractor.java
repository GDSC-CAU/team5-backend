package org.gdsccau.team5.safebridge.common.term;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataDto;
import org.springframework.stereotype.Component;

@Component
public class TermExtractor {

    private Trie trie;

    public TermExtractor() {
        Set<String> terms = TermLoader.loadTermsOnly();
        TrieBuilder builder = Trie.builder();
        for (String term : terms) {
            builder.addKeyword(term);
        }
        this.trie = builder.build();
    }

    public List<TermDataDto> query(final String chat) {
        Collection<Emit> emits = this.trie.parseText(chat);
        Set<String> terms = emits.stream()
                .map(Emit::getKeyword)
                .collect(Collectors.toSet());
        Set<String> finalTerms = new HashSet<>(terms);

        for (String term : terms) {
            for (int i = 1; i < term.length(); i++) {
                String subString = term.substring(0, i);
                finalTerms.remove(subString);
            }
        }

        return emits.stream()
                .filter(emit -> finalTerms.contains(emit.getKeyword()))
                .map(emit -> TermDataDto.builder()
                        .startIndex(emit.getStart())
                        .endIndex(emit.getEnd())
                        .term(emit.getKeyword())
                        .build())
                .toList();
    }
}
