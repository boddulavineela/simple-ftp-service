function proj_visuals

%Task 1

    data1 = load('task1/results_task1_gobackn.txt');
    data1(:, 1) = log2(data1(:, 1));
    generate_single_plot(data1, 'Transfer Time (sec) vs Window Size (log scale) (Go Back N)',...
                         'Window Size (log scale)', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 'task1/figure_task1_gobackn.eps', 1,...
                         '-rs', [.49 1 .63], 'g');
                     
    data2 = load('task1/results_task1_selrepeat.txt');
    data2(:, 1) = log2(data2(:, 1));
    generate_single_plot(data2, 'Transfer Time (sec) vs Window Size (log scale) (Sel. Repeat)',...
                         'Window Size (log scale)', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 'task1/figure_task1_selrepeat.eps', 1,...
                         '-.dm', [.49 1 .63], 'b');

    generate_single_plot_hold(data1, 'Transfer Time (sec) vs Window Size (log scale) (Go Back N)',...
                         'Window Size (log scale)', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 1, '-rs', [.49 1 .63], 'g');   
    
    generate_single_plot_hold(data2, 'Transfer Time (sec) vs Window Size (log scale) (Sel. Repeat)',...
                         'Window Size (log scale)', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 1 ,'-.dm', [.49 1 .63], 'b');
    
    title('Transfer Time (sec) vs Window Size (log scale)');   
    hTitle = get(gca, 'Title');
    hXLabel = get(gca, 'XLabel');
    hYLabel = get(gca, 'YLabel');        
    set(gca,'FontName','Helvetica')
    set([hTitle, hXLabel, hYLabel], 'FontName', 'Helvetica', 'FontWeight', 'bold');set([hXLabel, hYLabel], 'FontSize', 12)
    set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
    
    legend({'Go Back N', 'Conf Interval', 'Selective Repeat', 'Conf Interval'}, 'Location', 'Best');
    
    set(gcf, 'PaperPositionMode', 'auto');
    print('-depsc2', 'task1/figure_task1_combined.eps');    
    close;
    
%Task 2
    data1 = load('task2/results_task2_gobackn.txt');
    generate_single_plot(data1, 'Transfer Time (sec) vs Segment Size (bytes) (Go Back N)',...
                         'Segment Size (bytes)', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 'task2/figure_task2_gobackn.eps', 1,...
                         '-rs', [.49 1 .63], 'g');
    
    data2 = load('task2/results_task2_selrepeat.txt');
    generate_single_plot(data2, 'Transfer Time (sec) vs Segment Size (bytes) (Sel. Repeat)',...
                         'Segment Size (bytes)', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 'task2/figure_task2_selrepeat.eps', 1,...
                         '-.dm', [.49 1 .63], 'b');                 
    
    generate_single_plot_hold(data1, 'Transfer Time (sec) vs Segment Size (bytes) (Go Back N)',...
                         'Segment Size (bytes)', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 1, '-rs', [.49 1 .63], 'g');   
    
    generate_single_plot_hold(data2, 'Transfer Time (sec) vs Segment Size (bytes) (Sel. Repeat)',...
                         'Segment Size (bytes)', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 1 ,'-.dm', [.49 1 .63], 'b');
    title('Transfer Time (sec) vs Segment Size (bytes)');   
    hTitle = get(gca, 'Title');
    hXLabel = get(gca, 'XLabel');
    hYLabel = get(gca, 'YLabel');        
    set(gca,'FontName','Helvetica')
    set([hTitle, hXLabel, hYLabel], 'FontName', 'Helvetica', 'FontWeight', 'bold');set([hXLabel, hYLabel], 'FontSize', 12)
    set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
    
    legend({'Go Back N', 'Conf Interval', 'Selective Repeat', 'Conf Interval'}, 'Location', 'Best');
    
    set(gcf, 'PaperPositionMode', 'auto');
    print('-depsc2', 'task2/figure_task2_combined.eps');    
    close;  
    
%Task 3
    data1 = load('task3/results_task3_gobackn.txt');
    prob = 0.01:0.01:0.1;    
    data1 = [prob' data1];
    generate_single_plot(data1, 'Transfer Time (sec) vs Segment Loss Probability (Go Back N)' ,...
                         'Loss Probability', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 'task3/figure_task3_gobackn.eps', 1,...
                         '-rs', [.49 1 .63], 'g');
                     
    data2 = load('task3/results_task3_selrepeat.txt');
    prob = 0.01:0.01:0.1;    
    data2 = [prob' data2];
    generate_single_plot(data2, 'Transfer Time (sec) vs Segment Loss Probability (Sel. Repeat)' ,...
                         'Loss Probability', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 'task3/figure_task3_selrepeat.eps', 1,...
                         '-.dm', [.49 1 .63], 'b');                     
                     
    
    generate_single_plot_hold(data1, 'Transfer Time (sec) vs Segment Loss Probability (Go Back N)',...
                         'Loss Probability', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 1, '-rs', [.49 1 .63], 'g');   
    
    generate_single_plot_hold(data2, 'Transfer Time (sec) vs Segment Loss Probability (Sel. Repeat)',...
                         'Loss Probability', 'Transfer Time (sec)',...
                         {'Timeout = 50 msec'}, 1 ,'-.dm', [.49 1 .63], 'b');
    title('Transfer Time (sec) vs Loss Probability');   
    hTitle = get(gca, 'Title');
    hXLabel = get(gca, 'XLabel');
    hYLabel = get(gca, 'YLabel');        
    set(gca,'FontName','Helvetica')
    set([hTitle, hXLabel, hYLabel], 'FontName', 'Helvetica', 'FontWeight', 'bold');set([hXLabel, hYLabel], 'FontSize', 12)
    set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
    
    legend({'Go Back N', 'Conf Interval', 'Selective Repeat', 'Conf Interval'}, 'Location', 'Best');
    
    set(gcf, 'PaperPositionMode', 'auto');
    print('-depsc2', 'task3/figure_task3_combined.eps');    
    close;
end

function generate_single_plot_hold(data, ttl, xlbl, ylbl, lgnd, plotConfInterval,...
                                   lineStyle, markerFaceColor, markerEdgeColor)

figure(1);
confInterval = (data(:, 4) - data(:, 3))/2;

plot(data(:, 1), data(:, 2), lineStyle, 'LineWidth', 2, 'MarkerSize', 4, 'MarkerFaceColor' , markerFaceColor, 'MarkerEdgeColor', markerEdgeColor);
hold on;
confInterval = (data(:, 4) - data(:, 3))/2;
if (plotConfInterval)
    errorbar(data(:, 1), data(:, 2), confInterval);      
end
grid on;

hTitle = title(ttl);
hXLabel = xlabel(xlbl);
hYLabel = ylabel(ylbl);

set(gca,'FontName','Helvetica')

set([hTitle, hXLabel, hYLabel], 'FontName', 'Helvetica', 'FontWeight', 'bold');set([hXLabel, hYLabel], 'FontSize', 12)
set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
grid on
set(gca, ...
    'Box'         , 'off'     , ...
    'TickDir'     , 'out'     , ...
    'TickLength'  , [.02 .02] , ...
    'XMinorTick'  , 'on'      , ...
    'YMinorTick'  , 'on'      , ...
    'YGrid'       , 'on'      , ...
    'XColor'      , [.3 .3 .3], ...
    'YColor'      , [.3 .3 .3], ...
    'LineWidth'   , 1         );
axis fill
hold on;

end


function generate_single_plot(data, ttl, xlbl, ylbl, lgnd, outputfilename, plotConfInterval,...
                              lineStyle, markerFaceColor, markerEdgeColor)

figure(1);
confInterval = (data(:, 4) - data(:, 3))/2;

plot(data(:, 1), data(:, 2), lineStyle, 'LineWidth', 2, 'MarkerSize', 3, 'MarkerFaceColor' , markerFaceColor, 'MarkerEdgeColor', markerEdgeColor);
hold on;
confInterval = (data(:, 4) - data(:, 3))/2;
if (plotConfInterval)
    errorbar(data(:, 1), data(:, 2), confInterval);      
end
grid on;

hTitle = title(ttl);
hXLabel = xlabel(xlbl);
hYLabel = ylabel(ylbl);
hLegend = legend(lgnd, 'Location', 'Best');

set(gca,'FontName','Helvetica')

set([hTitle, hXLabel, hYLabel, hLegend], 'FontName', 'Helvetica', 'FontWeight', 'bold');
set([hLegend, gca], 'FontSize', 10);
set([hXLabel, hYLabel], 'FontSize', 12)
set(hTitle,'FontSize', 14, 'FontWeight', 'bold');
grid on
set(gca, ...
    'Box'         , 'off'     , ...
    'TickDir'     , 'out'     , ...
    'TickLength'  , [.02 .02] , ...
    'XMinorTick'  , 'on'      , ...
    'YMinorTick'  , 'on'      , ...
    'YGrid'       , 'on'      , ...
    'XColor'      , [.3 .3 .3], ...
    'YColor'      , [.3 .3 .3], ...
    'LineWidth'   , 1         );
axis fill

set(gcf, 'PaperPositionMode', 'auto');
print('-depsc2', outputfilename);
close;

end
