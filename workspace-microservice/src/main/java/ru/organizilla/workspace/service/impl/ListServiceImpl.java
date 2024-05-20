package ru.organizilla.workspace.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.organizilla.workspace.domain.Board;
import ru.organizilla.workspace.domain.ListEntity;
import ru.organizilla.workspace.dto.list.CreateListDto;
import ru.organizilla.workspace.dto.list.CreatedListInfoDto;
import ru.organizilla.workspace.dto.list.ReorderListDto;
import ru.organizilla.workspace.exception.NotAllowedException;
import ru.organizilla.workspace.repository.BoardRepository;
import ru.organizilla.workspace.repository.ListRepository;
import ru.organizilla.workspace.service.ListService;
import ru.organizilla.workspace.dao.UserDao;
import ru.organizilla.workspace.util.AccessCheckUtil;
import ru.organizilla.workspace.util.ListOrderUtil;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class ListServiceImpl implements ListService {

    private final ListRepository listRepository;
    private final BoardRepository boardRepository;

    private final AccessCheckUtil accessCheckUtil;
    private final ListOrderUtil listOrderUtil;

    private final UserDao userDao;

    private static final int POSITION_DELTA = 65_536;

    @Override
    public CreatedListInfoDto createList(CreateListDto listDto, String username) {
        var board = getBoardById(listDto.getBoardId());
        var user = userDao.getUserByUsername(username);

        if (!accessCheckUtil.canCreateUpdateDeleteCardAndList(user, board)) {
            throw new NotAllowedException("Creation not allowed");
        }

        var list = new ListEntity();
        list.setBoard(board);
        list.setName(listDto.getName());
        var previousBoardPosition = listRepository.findMaximumPositionByBoard(board).orElse(0);
        list.setPosition(previousBoardPosition + POSITION_DELTA);
        updateBoardLastActivity(board);

        return new CreatedListInfoDto(listRepository.save(list).getId());
    }

    @Override
    public void deleteList(Long listId, String username) {
        var user = userDao.getUserByUsername(username);
        var list = getListById(listId);

        if (!accessCheckUtil.canCreateUpdateDeleteCardAndList(user, list.getBoard())) {
            throw new NotAllowedException("Deletion not allowed");
        }

        updateBoardLastActivity(list.getBoard());
        listRepository.delete(list);
    }

    @Override
    public void reorderList(Long listId, ReorderListDto reorderListDto, String username) {
        var user = userDao.getUserByUsername(username);
        var list = getListById(listId);

        if (!accessCheckUtil.canCreateUpdateDeleteCardAndList(user, list.getBoard())) {
            throw new NotAllowedException("Creation not allowed");
        }

        listOrderUtil.changeListPosition(listId, reorderListDto.getPreviousListId(), reorderListDto.getNextListId());
    }

    @Override
    public void renameList(Long listId, String username, String newName) {
        var user = userDao.getUserByUsername(username);
        var list = getListById(listId);

        if (!accessCheckUtil.canCreateUpdateDeleteCardAndList(user, list.getBoard())) {
            throw new NotAllowedException("Renaming not allowed");
        }

        list.setName(newName);
        updateBoardLastActivity(list.getBoard());
        listRepository.save(list);
    }

    private Board getBoardById(Long id) {
        return boardRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found: " + id));
    }

    private ListEntity getListById(Long id) {
        return listRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("List not found: " + id));
    }

    private void updateBoardLastActivity(Board board) {
        board.setLastActivity(new Timestamp(System.currentTimeMillis()));
        boardRepository.save(board);
    }
}
